package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    private final UserRepo userRepo;

    public ProfileController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/profile")
    public String member_profile(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "member_profile";
    }

    @PostMapping("/edit_profile")
    public String edit_profile(
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            HttpSession session,
            Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Name cannot be empty.");
            model.addAttribute("user", currentUser);
            return "member_profile";
        }

        if (!name.matches("^[a-zA-ZÀ-ỹ\\s]{2,50}$")) {
            model.addAttribute("error", "Name must be 2-50 characters and contain only letters.");
            model.addAttribute("user", currentUser);
            return "member_profile";
        }

        if (!gender.equalsIgnoreCase("male") &&
                !gender.equalsIgnoreCase("female") &&
                !gender.equalsIgnoreCase("other")) {
            model.addAttribute("error", "Invalid gender selected.");
            model.addAttribute("user", currentUser);
            return "member_profile";
        }

        currentUser.setUsername(name.trim());
        currentUser.setGender(gender.toLowerCase());
        userRepo.save(currentUser);
        session.setAttribute("user", currentUser);

        model.addAttribute("success", "Profile updated successfully.");
        model.addAttribute("user", currentUser);
        return "member_profile";
    }


    @PostMapping("/change_password")
    public String change_password(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model
    ) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", currentUser);

        if (!currentUser.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Old password is incorrect.");
            return "member_profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "member_profile";
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])\\S{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            model.addAttribute("error",
                    "Password must be at least 8 characters long, include uppercase and lowercase letters, a number, and a special character. Spaces are not allowed.");
            return "member_profile";
        }

        currentUser.setPassword(newPassword);
        userRepo.save(currentUser);
        model.addAttribute("success", "Password changed successfully.");

        return "member_profile";
    }

}