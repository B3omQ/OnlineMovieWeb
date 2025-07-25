package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.entities.WatchHistory;
import fa.project.onlinemovieweb.repo.HistoryRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

        List<WatchHistory> allHistory = historyRepo.findByUserOrderByWatchedAtDesc(user);
        List<WatchHistory> latestPerMedia = getLatestWatchPerMedia(allHistory);
        model.addAttribute("historyList", latestPerMedia);
        return "history";
    }

    private List<WatchHistory> getLatestWatchPerMedia(List<WatchHistory> fullHistory) {
        Map<Long, WatchHistory> latestMap = new LinkedHashMap<>();

        for (WatchHistory wh : fullHistory) {
            if (wh.getMedia() == null) continue;

            Long mediaId = wh.getMedia().getId();
            if (!latestMap.containsKey(mediaId)) {
                latestMap.put(mediaId, wh);
            }
        }

        return new ArrayList<>(latestMap.values());
    }
}
