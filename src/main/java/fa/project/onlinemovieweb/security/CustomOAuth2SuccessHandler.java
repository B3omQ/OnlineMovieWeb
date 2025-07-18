package fa.project.onlinemovieweb.security;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public CustomOAuth2SuccessHandler(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");

        User user = userRepo.findByEmail(email);
        HttpSession session = request.getSession();

        if (user != null && !user.isOauthUser()) {
            session.setAttribute("error", "An account with that email already exists. Please login with email and password.");
            response.sendRedirect("/login?googleError");
            return;
        }

        if (user == null) {
            String randomPassword = generateRandomPassword();
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(randomPassword));
            newUser.setRole(Role.USER);
            newUser.setOauthUser(true);
            userRepo.save(newUser);

            session.setAttribute("userIdToComplete", newUser.getId());
            response.sendRedirect("/complete-profile");
            return;
        } else if (user.getUsername() == null || user.getUsername().isEmpty()) {
            session.setAttribute("userIdToComplete", user.getId());
            response.sendRedirect("/complete-profile");
            return;
        }

        session.setAttribute("user", user);
        response.sendRedirect("/home");
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return passwordEncoder.encode(java.util.Base64.getEncoder().encodeToString(bytes));
    }
}