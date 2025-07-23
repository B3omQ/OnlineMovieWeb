package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.dto.UserChangePasswordDto;
import fa.project.onlinemovieweb.dto.UserRegistrationDto;
import fa.project.onlinemovieweb.entities.Favorite;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.entities.WatchHistory;
import fa.project.onlinemovieweb.repo.FavoriteRepo;
import fa.project.onlinemovieweb.repo.HistoryRepo;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<User> users = userRepo.findAllByUsernameIgnoreCase(name);
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


        if (!gender.equalsIgnoreCase("male") &&
                !gender.equalsIgnoreCase("female") &&
                !gender.equalsIgnoreCase("other")) {
            model.addAttribute("error", "Invalid gender selected.");
            model.addAttribute("user", currentUser);
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

        if(users.stream().anyMatch(u -> u.getUsername().equals(name))){
            model.addAttribute("error", "This username is already taken.");
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

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long.");
            model.addAttribute("user", currentUser);
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }


        if (newPassword.contains(" ")) {
            model.addAttribute("error", "Password must not contain spaces.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("error", "Old password is incorrect.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }


        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            model.addAttribute("userDtoCp", new UserChangePasswordDto());
            return "member_profile";
        }


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

    @Autowired
    private FavoriteRepo favoriteRepo;

    @GetMapping("/user/favorite")
    public String viewFavorite(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);


        List<Favorite> favoriteList = favoriteRepo.findByUser(user);

        List<Media> mediaList = favoriteList.stream()
                .map(Favorite::getMedia)
                .collect(Collectors.toList());

        model.addAttribute("mediaList", mediaList);
        return "favorite";
    }
}