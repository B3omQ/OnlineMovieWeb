package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.dto.UserRegistrationDto;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
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

    		if (exactMatch == null || !passwordEncoder.matches(password, exactMatch.getPassword())) {
    		    model.addAttribute("error", "Invalid username or password");
    		    return "login";
    		}

        session.setAttribute("user", exactMatch);
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
