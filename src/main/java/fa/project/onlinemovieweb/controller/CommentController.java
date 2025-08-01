package fa.project.onlinemovieweb.controller;
import fa.project.onlinemovieweb.entities.*;
import fa.project.onlinemovieweb.repo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class CommentController {
    @Autowired
    CommentRepo commentRepo;

    @Autowired
    private MediaRepo mediaRepo;

    @PostMapping("/media/comment/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId, HttpSession session, Model model) {
        Comment comment = commentRepo.findById(commentId).get();
        User user = (User) session.getAttribute("user");

        boolean liked;
        if (comment.getLikedByUsers().contains(user)) {
            comment.getLikedByUsers().remove(user);
            liked = false;
        } else {
            comment.getLikedByUsers().add(user);
            liked = true;
        }
        commentRepo.save(comment);
        model.addAttribute("user", user);
        return ResponseEntity.ok(Map.of("success", true, "liked", liked, "likeCount", comment.getLikedByUsers().size()));
    }

    @PostMapping("/media/{mediaId}/comment/{parentId}/reply")
    public String replyToComment(@PathVariable Long mediaId,
                                 @PathVariable Long parentId,
                                 @RequestParam String content,
                                 HttpSession session, Model model) {
        Comment parent = commentRepo.findById(parentId).get();
        Media media = mediaRepo.findById(mediaId).get();
        User user = (User) session.getAttribute("user");

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setMedia(media);
        reply.setParent(parent);
        reply.setUser(user);
        reply.setCreatedAt(LocalDateTime.now());
        commentRepo.save(reply);
        String redirectUrl = "/media/" + mediaId;
        model.addAttribute("c", reply);
        return "redirect:/media/" + mediaId;
    }

}
