package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.*;
import fa.project.onlinemovieweb.repo.CommentRepo;
import fa.project.onlinemovieweb.repo.FavoriteRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import fa.project.onlinemovieweb.repo.ReviewRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MediaProfileController {
    @Autowired
    private MediaRepo mediaRepo;

    @Autowired
    CommentRepo commentRepo;

    @GetMapping("/media/{id}")
    public String getMedia(@PathVariable Long id, Model model, HttpSession session,
                           @RequestParam(name = "ep", required = false, defaultValue = "1") int episodeNumber,
                           @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Episode> episodes = new ArrayList<>();
        Episode selectedEpisode = null;
        int season = 1;

        if ("TV Show".equalsIgnoreCase(media.getType())) {
            episodes = media.getEpisodes().stream()
                    .sorted(Comparator.comparingInt(Episode::getEpisodeNumber))
                    .toList();

            selectedEpisode = episodes.stream()
                    .filter(ep -> ep.getEpisodeNumber() == episodeNumber)
                    .findFirst()
                    .orElse(null);

            if (selectedEpisode != null) {
                season = selectedEpisode.getSeason();
            }
        }

        model.addAttribute("media", media);
        model.addAttribute("episodes", episodes);
        model.addAttribute("selectedEpisode", selectedEpisode);
        model.addAttribute("episode", episodeNumber);
        model.addAttribute("season", season);

        List<Media> mediaList = mediaRepo.findAll();
        model.addAttribute("mediaList", mediaList);

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        int pageSize = 5;
        Page<Comment> commentPage;

        commentPage = commentRepo.findByMediaIdOrderByCreatedAtDesc(
                media.getId(), PageRequest.of(page, pageSize));

        model.addAttribute("commentPage", commentPage);
        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());

        boolean isFavorite = false;
        if (user != null) {
            isFavorite = favoriteRepo.findByUserAndMedia(user, media).isPresent();
        }
        model.addAttribute("isFavorite", isFavorite);

        List<Review> reviews = reviewRepo.findByMedia(media);
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", Math.round(averageRating * 10.0) / 10.0);
        Map<Integer, Long> ratingCount = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCount.put(i, 0L);
        }
        reviews.forEach(review -> {
            int rating = review.getRating();
            ratingCount.put(rating, ratingCount.getOrDefault(rating, 0L) + 1);
        });
        long totalReviews = reviews.size();
        Map<Integer, Long> ratingCountMap = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        Map<Integer, Integer> ratingPercentage = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = ratingCountMap.getOrDefault(i, 0L);
            int percent = totalReviews > 0 ? (int) Math.round((count * 100.0) / totalReviews) : 0;
            ratingPercentage.put(i, percent);
        }
        model.addAttribute("ratingCount", ratingCount);
        model.addAttribute("ratingPercentage", ratingPercentage);
        model.addAttribute("totalReviews", totalReviews);
        return "media_profile";
    }

    @PostMapping("/media/{id}/comment")
    public String postMediaComment(@PathVariable Long id,
                                   @RequestParam String content,
                                   HttpSession session, Model model,
                                   @RequestParam(name = "ep", required = false, defaultValue = "1") int episodeNumber) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setMedia(media);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        // Only set episode if the media is a TV Show AND a matching episode exists

        commentRepo.save(comment);

        // Redirect back to the same media page with appropriate episode param
        String redirectUrl = "/media/" + id;
        model.addAttribute("c", comment);
        return "fragments/comment :: comment";
    }

    @Autowired
    private FavoriteRepo favoriteRepo;

    @PostMapping("/media/{id}/favorite")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Please log in first"));
        }

        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Optional<Favorite> existing = favoriteRepo.findByUserAndMedia(user, media);

        boolean favorited;

        if (existing.isPresent()) {
            favoriteRepo.delete(existing.get());
            favorited = false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setMedia(media);
            favorite.setAddedAt(LocalDateTime.now());
            favoriteRepo.save(favorite);
            favorited = true;
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "favorited", favorited,
                "message", favorited ? "Added to favorites" : "Removed from favorites"
        ));
    }

    @Autowired
    private ReviewRepo reviewRepo;

    @PostMapping("/media/{id}/review")
    @ResponseBody
    public Map<String, Object> postReview(@PathVariable Long id,
                                          @RequestBody Map<String, String> payload,
                                          HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            response.put("message", "You must be logged in.");
            return response;
        }

        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int rating = Integer.parseInt(payload.get("rating"));
        String content = payload.get("content");

        Review review = new Review();
        review.setUser(user);
        review.setMedia(media);
        review.setRating(rating);
        review.setContent(content);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepo.save(review);

        response.put("success", true);
        return response;
    }

}
