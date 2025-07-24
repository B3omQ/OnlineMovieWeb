package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.dto.UserRegistrationDto;
import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import fa.project.onlinemovieweb.service.EmailService;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class RegisterController {
	
	@Autowired
    private UserRepo userRepo;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    public String register(Model model){
    	model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("userDto") UserRegistrationDto userDto, Model model){
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        List<User> users = userRepo.findAllByUsernameIgnoreCase(userDto.getUsername());
        if (!Pattern.matches(emailRegex, userDto.getEmail())) {
            model.addAttribute("error", "Invalid email format");
            return "register";
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        if(userDto.getPassword().contains(" ")) {
        	model.addAttribute("error", "Passwords can not contain white space");
        	return "register";
        }
        if(userDto.getConfirmPassword().contains(" ")) {
        	model.addAttribute("error", "Passwords can not contain white space");
        	return "register";
        }
        if(userDto.getPassword().length() < 6) {
        	model.addAttribute("error","Password need to be at least 6 characters long");
        	return "register";
        }
        if (users.stream().anyMatch(u -> u.getUsername().equals(userDto.getUsername()))) {
            model.addAttribute("error", "Username is already in use");
            return "register";
        }
        if (userRepo.findByEmail(userDto.getEmail()) != null) {
            model.addAttribute("error", "Email is already in use");
            return "register";
        }
        User user = new User();
        user.setAvatar("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-tuRFyRupR6eIMCFvaXBnVWJ9x9ghHyZ1IQ&s");
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.USER);
        user.setOauthUser(false);
		user.setEnabled(false);
		user.setVerificationToken(UUID.randomUUID().toString());
        userRepo.save(user);
		emailService.sendVerifyMessage(
				userDto.getEmail(),
	            "Please verify your email by clicking the link below",
	            "Here is your link: " + "http://localhost:8080/api/verify?token=" + user.getVerificationToken() + " ."
	        );
		model.addAttribute("error", "A verification link has been sent to your email. Please access it to verify your email right away");
        return "login";
    }
    public String verifyUser(@RequestParam String email, Model model) {
    		User user = userRepo.findByEmail(email);
    		user.setEnabled(false);
    		user.setVerificationToken(UUID.randomUUID().toString());
    		userRepo.save(user);
    		emailService.sendVerifyMessage(
    				email,
    	            "Please verify your email by clicking the link below",
    	            "Here is your link: " + "http://localhost:8080/api/verify?token=" + user.getVerificationToken() + " ."
    	        );
            model.addAttribute("message", "A verification link has been sent to your email.");
            return "redirect:/login";
    }
}
