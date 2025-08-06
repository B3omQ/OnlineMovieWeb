package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Episode;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.Notification;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.NotificationRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class NotifyController {

    @Autowired
    private NotificationRepo notificationRepo;

    @GetMapping("/notify")
    public String getNotify(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        List<Notification> notifications = notificationRepo.findAllByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user));
        model.addAttribute("notification_page", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "notify";
    }

    @GetMapping("/notification/read/{id}")
    public String readNotificationAndRedirect(@PathVariable Long id) {
        Notification notification = notificationRepo.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepo.save(notification);
            if(notification.getType().equals("Episode")) {
                Episode episode = notification.getEpisode();
                if (episode != null) {
                    String mediaTitle = episode.getMedia().getTitle().replaceAll(" ", "-").toLowerCase();
                    Long mediaId = episode.getMedia().getId();
                    int episodeNumber = episode.getEpisodeNumber();

                    return "redirect:/mediaVideo/" + mediaTitle + "." + mediaId + "?ep=" + episodeNumber + "&season=" + episode.getSeason();
                }
            }
            else if(notification.getType().equals("Mention")) {
                Media media = notification.getComment().getMedia();
                Episode episode = notification.getComment().getEpisode();
                if(episode != null) {
                    media = episode.getMedia();
                    String slug = media.getTitle().toLowerCase().replaceAll(" ", "-");
                    return "redirect:/mediaVideo/" + slug + "." + media.getId()
                            + "?ep=" + episode.getEpisodeNumber()
                            + "&season=" + episode.getSeason();
                }
                else if(media != null) {
                    return "redirect:/media/" + media.getId();
                }
            }
        }
        return "redirect:/notify";
    }
}
