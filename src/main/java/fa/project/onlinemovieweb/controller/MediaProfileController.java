package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Comment;
import fa.project.onlinemovieweb.entities.Episode;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.CommentRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

        if (selectedEpisode != null) {
            commentPage = commentRepo.findByEpisodeIdOrderByCreatedAtDesc(
                    selectedEpisode.getId(), PageRequest.of(page, pageSize));
        } else {
            commentPage = commentRepo.findByMediaIdOrderByCreatedAtDesc(
                    media.getId(), PageRequest.of(page, pageSize));
        }

        model.addAttribute("commentPage", commentPage);
        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());

        return "media_profile";
    }

    @PostMapping("/media/{id}/comment")
    public String postMediaComment(@PathVariable Long id,
                                   @RequestParam String content,
                                   HttpSession session,
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

        return "redirect:" + redirectUrl;
    }

}
