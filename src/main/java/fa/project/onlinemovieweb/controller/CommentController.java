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

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotificationRepo notificationRepo;

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
                                 @RequestParam Long taggedUserId,
                                 HttpSession session, Model model) {
        Comment parent = commentRepo.findById(parentId).get();
        Media media = mediaRepo.findById(mediaId).get();
        User user = (User) session.getAttribute("user");
        User taggedUser = userRepo.findById(taggedUserId).get();
        Comment reply = new Comment();
        reply.setContent(content);
        reply.setMedia(media);
        reply.setParent(parent);
        reply.setUser(user);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setTaggedUser(taggedUser);
        commentRepo.save(reply);
        String redirectUrl = "/media/" + mediaId;
        model.addAttribute("c", reply);
        if (!taggedUser.getId().equals(user.getId())) {
            Notification notification = new Notification();
            notification.setUser(taggedUser);
            notification.setTriggeredBy(user);
            notification.setComment(reply);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setType("Mention");
            notificationRepo.save(notification);
        }
        return "redirect:/media/" + mediaId;
    }

}
