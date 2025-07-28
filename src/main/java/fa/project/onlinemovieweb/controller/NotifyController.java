package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Episode;
import fa.project.onlinemovieweb.entities.Notification;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.NotificationRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        return "notify";
    }

    @GetMapping("/notification/read/{id}")
    public String readNotificationAndRedirect(@PathVariable Long id) {
        Notification notification = notificationRepo.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepo.save(notification);

            Episode episode = notification.getEpisode();
            if (episode != null) {
                String mediaTitle = episode.getMedia().getTitle().replaceAll(" ", "-").toLowerCase();
                Long mediaId = episode.getMedia().getId();
                int episodeNumber = episode.getEpisodeNumber();

                return "redirect:/mediaVideo/" + mediaTitle + "." + mediaId + "?ep=" + episodeNumber;
            }
        }
        return "redirect:/notify";
    }
}
