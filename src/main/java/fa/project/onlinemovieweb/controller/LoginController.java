package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.dto.UserRegistrationDto;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import fa.project.onlinemovieweb.service.EmailService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
    private UserRepo userRepo;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
    	List<User> matches = userRepo.findByUsername(username);
    	User exactMatch = matches.stream()
    		    .filter(u -> u.getUsername().equals(username))
    		    .findFirst()
    		    .orElse(null);
    	
    		if (exactMatch != null && exactMatch.isOauthUser()) {
    			model.addAttribute("error", "This account was signed up using Google, please use Google to login.");
    			return "login";
    		}
    		
    		if(password.contains(" ")) {
    			model.addAttribute("error", "Password can not contain white space");
    			return "login";
    		}

    		if (exactMatch == null || !passwordEncoder.matches(password, exactMatch.getPassword())) {
    		    model.addAttribute("error", "Invalid username or password");
    		    return "login";
    		}

        session.setAttribute("user", exactMatch);
        return "redirect:/home";
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "No account found with that email.");
            return "forgot-password";
        }

        if (user.isOauthUser()) {
            model.addAttribute("error", "This account uses Google login. Please sign in using Google.");
            return "login";
        }

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        emailService.sendSimpleMessage(
            email,
            "Your New Password",
            "Here is your new password: " + newPassword + "\nPlease login and change it immediately."
        );

        model.addAttribute("message", "A new password has been sent to your email.");
        return "reset-success";
    }

    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String all = upper + lower + digits;
        int length = 6 + (int)(Math.random() * 3);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = (int)(Math.random() * all.length());
            sb.append(all.charAt(idx));
        }
        return sb.toString();
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
