package fa.project.onlinemovieweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fa.project.onlinemovieweb.service.UserService;

@Controller
@RequestMapping("/api")
public class VerificationController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            return ResponseEntity.ok("Account verified successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid or expired token.");
    }
}