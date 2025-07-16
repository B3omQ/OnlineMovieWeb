package fa.project.onlinemovieweb.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ProfileController {

  @GetMapping("/member_profile")
    public String member_profile(HttpSession session,  Model model) {
      Object user = session.getAttribute("user");
      if (user == null) {
          return "redirect:/login";
      }
      model.addAttribute("user", user);
      return "member_profile";
    }
}