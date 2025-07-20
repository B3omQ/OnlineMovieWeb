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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MediaRepo mediaRepo;

    @Autowired
    private GenreRepo genreRepo;

    @GetMapping("admin/")
    public String getAdmin(){
        return "redirect:/admin/medias";
    }

    @GetMapping("admin/medias")
    public String getMedias(Model model, @RequestParam(required = false) String query){
        List<Media> medias = mediaRepo.findAll();
        model.addAttribute("medias", medias);
        model.addAttribute("query", query);
        return "admin_medias";
    }

    @GetMapping("admin/medias/update/{id}")
    public String getMedia(@PathVariable Long id, Model model){
        Media media = mediaRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("media", media);
        List<Genre> genres = genreRepo.findAll();
        model.addAttribute("genres", genres);
        return "update_media";
    }

    @PostMapping("admin/medias/update")
    public String updateMedia(@ModelAttribute Media media, @RequestParam(required = false) List<Integer> genreIds, RedirectAttributes redirectAttributes){
        Media m = mediaRepo.findById(media.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        m.setTitle(media.getTitle());
        m.setDescription(media.getDescription());
        m.setReleaseYear(media.getReleaseYear());
        m.setLanguage(media.getLanguage());
        m.setType(media.getType());
        m.setVideoUrl(media.getVideoUrl());
        m.setPoster(media.getPoster());
        m.setBanner(media.getBanner());
        if(genreIds==null || genreIds.isEmpty()){
            m.setGenres(new ArrayList<>());
        }
        else{
            List<Genre> genres = genreRepo.findAllById(genreIds);
            m.setGenres(genres);
        }
        mediaRepo.save(m);
        redirectAttributes.addAttribute("query", m.getTitle());
        return "redirect:/admin/medias";
    }


    @GetMapping("/admin/users")
    public String getUsers(Model model){
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "admin_users";
    }


}
