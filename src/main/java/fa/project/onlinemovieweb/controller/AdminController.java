package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.GenreRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import fa.project.onlinemovieweb.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MediaRepo mediaRepo;

    @Autowired
    private GenreRepo genreRepo;

    @GetMapping("admin/")
    public String getAdmin() {
        return "redirect:/admin/medias";
    }

    @GetMapping("admin/medias")
    public String getMedias(Model model, @RequestParam(required = false) String query) {
        List<Media> medias = mediaRepo.findAll();
        model.addAttribute("medias", medias);
        model.addAttribute("query", query);
        return "admin_medias";
    }

    @GetMapping("admin/medias/update/{id}")
    public String getUpdate(@PathVariable Long id, Model model) {
        Media media = mediaRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("media", media);
        List<Genre> genres = genreRepo.findAll();
        model.addAttribute("genres", genres);
        model.addAttribute("isUpdate", true);
        return "update_media";
    }

    @PostMapping("admin/medias/update")
    public String updateMedia(@ModelAttribute Media media, @RequestParam(required = false) List<Long> genreIds,
                              @RequestParam(required = false) MultipartFile bannerFile, @RequestParam(required = false) MultipartFile posterFile,
                              @RequestParam(required = false) String bannerUrl, @RequestParam(required = false) String posterUrl,
                              RedirectAttributes redirectAttributes) {
        Media m = mediaRepo.findById(media.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        m.setTitle(media.getTitle());
        m.setDescription(media.getDescription());
        m.setReleaseYear(media.getReleaseYear());
        m.setLanguage(media.getLanguage());
        m.setType(media.getType());
        m.setVideoUrl(media.getVideoUrl());

        if (genreIds == null || genreIds.isEmpty()) {
            m.setGenres(new ArrayList<>());
        } else {
            List<Genre> genres = genreRepo.findAllById(genreIds);
            m.setGenres(genres);
        }

        if(bannerFile != null && !bannerFile.isEmpty()){
            String bannerPath = uploadImage(bannerFile, "banners");
            if(bannerPath!= null){
                m.setBanner(bannerPath);
            }
        }
        else if(bannerUrl != null && !bannerUrl.trim().isEmpty()){
            m.setBanner(bannerUrl.trim());
        }

        if(posterFile != null && !posterFile.isEmpty()){
            String posterPath = uploadImage(posterFile, "posters");
            if(posterPath!= null){
                m.setPoster(posterPath);
            }
        }
        else if(posterUrl != null && !posterUrl.trim().isEmpty()){
            m.setPoster(posterUrl.trim());
        }

        mediaRepo.save(m);
        redirectAttributes.addAttribute("query", m.getTitle());
        return "redirect:/admin/medias";
    }

    @GetMapping("/admin/medias/create")
    public String getCreate(Model model) {
        Media media = new Media();
        model.addAttribute("media", media);
        List<Genre> genres = genreRepo.findAll();
        model.addAttribute("genres", genres);
        model.addAttribute("isUpdate", false);
        return "update_media";
    }

    @PostMapping("/admin/medias/create")
    public String createMedia(@ModelAttribute Media media, @RequestParam(required = false) List<Long> genreIds,
                              @RequestParam(required = false) MultipartFile bannerFile, @RequestParam(required = false) MultipartFile posterFile,
                              @RequestParam(required = false) String bannerUrl, @RequestParam(required = false) String posterUrl,
                              RedirectAttributes redirectAttributes) {
        if (media != null) {
            if (genreIds == null || genreIds.isEmpty()) {
                media.setGenres(new ArrayList<>());
            } else {
                List<Genre> genres = genreRepo.findAllById(genreIds);
                media.setGenres(genres);
            }

            if(bannerFile != null && !bannerFile.isEmpty()){
                String bannerPath = uploadImage(bannerFile, "banners");
                if(bannerPath != null){
                    media.setBanner(bannerPath);
                }
            }
            else if(bannerUrl != null && !bannerUrl.trim().isEmpty()){
                media.setBanner(bannerUrl.trim());
            }

            if(posterFile != null && !posterFile.isEmpty()){
                String posterPath = uploadImage(posterFile, "posters");
                if(posterPath != null){
                    media.setPoster(posterPath);
                }
            }
            else if(posterUrl != null && !posterUrl.trim().isEmpty()){
                media.setPoster(posterUrl.trim());
            }

            mediaRepo.save(media);
            redirectAttributes.addAttribute("query", media.getTitle());
        }
        return "redirect:/admin/medias";
    }

    @GetMapping("/admin/medias/delete/{id}")
    public String deleteMedia(@PathVariable Long id) {
        Media m = mediaRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediaRepo.delete(m);
        return "redirect:/admin/medias";
    }

    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "admin_users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        User u = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userRepo.delete(u);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/updateRole/{id}")
    public String updateRole(@PathVariable Long id, RedirectAttributes redirectAttributes){
        User u = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(u.getRole().equals(Role.USER)){
            u.setRole(Role.ADMIN);
        }
        else{
            u.setRole(Role.USER);
        }
        userRepo.save(u);
        redirectAttributes.addAttribute("query", u.getUsername());
        return "redirect:/admin/users";
    }

    public String uploadImage(MultipartFile image, String folder){
        File dir = new File("assets/" + folder);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        try{
            File file = new File(dir, filename);
            image.transferTo(file);
            return "/assets/" + folder + "/" + filename;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
