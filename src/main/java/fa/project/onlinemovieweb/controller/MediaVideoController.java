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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.PageRequest;

@Controller
public class MediaVideoController {

    @Autowired
    MediaRepo mediaRepo;

    @Autowired
    CommentRepo commentRepo;

    @GetMapping("/mediaVideo/{slug}.{id}")
    public String viewMediaVideo(HttpSession session, Model model,
                                 @PathVariable Long id,
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

        return "mediaVideo";
    }

   @PostMapping("/mediaVideo/{slug}.{id}/comment")
    public String postComment(@PathVariable Long id,
                              @RequestParam(name = "ep", required = false) Integer episodeNumber,
                              @RequestParam("content") String content,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null || content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please log in and write something.");
            return "redirect:/mediaVideo/slug." + id + (episodeNumber != null ? "?ep=" + episodeNumber : "");
        }

        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent(content);

        if (episodeNumber != null && "TV Show".equalsIgnoreCase(media.getType())) {
            Episode episode = media.getEpisodes().stream()
                    .filter(ep -> ep.getEpisodeNumber() == episodeNumber)
                    .findFirst()
                    .orElse(null);
            if (episode != null) {
                comment.setEpisode(episode);
                comment.setMedia(media);
            }
        } else {
            comment.setMedia(media);
        }

        commentRepo.save(comment);

       model.addAttribute("c", comment); // dùng để hiển thị trong fragment
       return "fragments/comment :: comment"; // Trả về đoạn HTML comment nhỏ
    }


}
