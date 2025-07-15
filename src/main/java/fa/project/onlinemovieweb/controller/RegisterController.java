package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegisterController {
    private final UserRepo userRepo;

    public RegisterController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model){
        if(userRepo.findByEmail(user.getEmail())!=null){
            model.addAttribute("error","Email is already in use");
            return "redirect:/register";
        }
        user.setRole(Role.USER);
        userRepo.save(user);
        return "redirect:/login";
    }
}
