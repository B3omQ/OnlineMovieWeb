package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotifyController {
    @GetMapping("/notify")
    public String getNotify(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "notify";
    }
}
