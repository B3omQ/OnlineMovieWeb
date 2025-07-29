package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Favorite;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.Notification;
import fa.project.onlinemovieweb.repo.FavoriteRepo;
import fa.project.onlinemovieweb.repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import fa.project.onlinemovieweb.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FavoriteController {
    @Autowired
    private FavoriteRepo favoriteRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @GetMapping("/favorite")
    public String viewFavourite(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Favorite> userList = favoriteRepo.findByUser(user);
        List<Media> favoriteList = userList.stream()
                .map(fav -> fav.getMedia())
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("mediaList", favoriteList);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "favorite";
    }
}
