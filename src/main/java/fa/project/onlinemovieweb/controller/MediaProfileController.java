package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.repo.MediaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class MediaProfileController {
    @Autowired
    private MediaRepo mediaRepo;

    @GetMapping("/media/{id}")
    public String getMedia(@PathVariable Long id, Model model) {
        Optional<Media> media = mediaRepo.findById(id);
        model.addAttribute("media", media.get());
        return "media_profile";
    }
}
