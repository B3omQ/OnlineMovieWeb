package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.dto.UserChangePasswordDto;
import fa.project.onlinemovieweb.dto.UserRegistrationDto;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class ProfileController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ProfileController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/profile")
    public String member_profile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("userDtoCp", new UserChangePasswordDto());
        model.addAttribute("user", currentUser);
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
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

//        if (!name.matches("^[a-zA-ZÀ-ỹ](?:[a-zA-ZÀ-ỹ\\s]{0,48}[a-zA-ZÀ-ỹ])?$")) {
//            model.addAttribute("error", "Name must be 2-50 characters, letters only, no leading/trailing spaces.");
//            model.addAttribute("user", currentUser);
//            model.addAttribute("userDtoCp", new UserChangePasswordDto());
//            return "member_profile";
//        }


        if (!gender.equalsIgnoreCase("male") &&
                !gender.equalsIgnoreCase("female") &&
                !gender.equalsIgnoreCase("other")) {
            model.addAttribute("error", "Invalid gender selected.");
            model.addAttribute("user", currentUser);
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

        currentUser.setUsername(name.trim());
        currentUser.setGender(gender.toLowerCase());
        userRepo.save(currentUser);
        session.setAttribute("user", currentUser);

        model.addAttribute("success", "Profile updated successfully.");
        model.addAttribute("user", currentUser);
        model.addAttribute("userDtoCp", new UserChangePasswordDto());
        return "member_profile";
    }


    @PostMapping("/change_password")
    public String change_password(
            @ModelAttribute("userDtoCp") UserChangePasswordDto userDtoCp,
            HttpSession session,
            Model model
    ) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);

        String oldPassword = userDtoCp.getPassword();
        String newPassword = userDtoCp.getNewPassword();
        String confirmPassword = userDtoCp.getConfirmPassword();

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("error", "Old password is incorrect.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

        if (newPassword.contains(" ")) {
            model.addAttribute("error", "Password must not contain spaces.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

//        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])\\S{8,}$";
//        if (!newPassword.matches(passwordRegex)) {
//            model.addAttribute("error",
//                    "Password must be at least 8 characters long, include uppercase and lowercase letters, a number, and a special character. Spaces are not allowed.");
//            model.addAttribute("userDtoCp", new UserChangePasswordDto());
//            return "member_profile";
//        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(currentUser);

        model.addAttribute("success", "Password changed successfully.");
        model.addAttribute("userDtoCp", new UserChangePasswordDto());
        return "member_profile";
    }



    @PostMapping("/upload_avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                               HttpSession session,
                               Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", currentUser);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "Please choose a file.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

        try {
            String oldAvatar = currentUser.getAvatar();
            if (oldAvatar != null && !oldAvatar.isEmpty()) {
                String oldFilePath = "." + oldAvatar;
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            String shortId = UUID.randomUUID().toString().substring(0, 8);
            String filename = shortId + "_" + file.getOriginalFilename();
//            String filename = file.getOriginalFilename();
            String uploadDir = "assets/avatars/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();


            Path path = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


            currentUser.setAvatar("/" + uploadDir + filename);
            userRepo.save(currentUser);
            session.setAttribute("user", currentUser);

            model.addAttribute("success", "Avatar updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Upload failed.");
        }
        model.addAttribute("userDtoCp", new UserChangePasswordDto());
        return "member_profile";
    }


}