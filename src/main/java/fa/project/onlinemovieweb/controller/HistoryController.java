package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.entities.WatchHistory;
import fa.project.onlinemovieweb.repo.HistoryRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HistoryController {

    @Autowired
    private HistoryRepo historyRepo;

    @GetMapping("/history")
    public String viewWatchHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        List<WatchHistory> watchHistoryList = historyRepo.findByUserOrderByWatchedAtDesc(user);

        List<Media> watchedMedia = watchHistoryList.stream()
                .map(WatchHistory::getMedia)
                .collect(Collectors.toList());

        model.addAttribute("watchedMedia", watchedMedia);
        model.addAttribute("historyList", watchHistoryList);
        return "history";
    }
}
