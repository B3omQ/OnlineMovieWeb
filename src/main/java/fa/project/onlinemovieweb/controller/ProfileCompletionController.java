package fa.project.onlinemovieweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileCompletionController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/complete-profile")
    public String completeProfilePage(HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userIdToComplete");
        if (userIdObj == null) return "redirect:/login";
        model.addAttribute("error", null);
        return "complete-profile";
    }

    @PostMapping("/complete-profile")
    public String completeProfile(@RequestParam String username, HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userIdToComplete");
        if (userIdObj == null) return "redirect:/login";
        Long userId = (Long) userIdObj;

        if (userRepo.findByUsername(username) != null) {
            model.addAttribute("error", "Username is already taken");
            return "complete-profile";
        }

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        user.setUsername(username);
        userRepo.save(user);

        session.removeAttribute("userIdToComplete");
        session.setAttribute("user", user);
        return "redirect:/home";
    }
}