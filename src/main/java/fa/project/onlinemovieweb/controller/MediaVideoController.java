package fa.project.onlinemovieweb.controller;


import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.MediaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class MediaVideoController {

    @Autowired
    MediaRepo mediaRepo;

    @GetMapping("/mediaVideo/{slug}.{id}")
    public String viewMediaVideo(HttpSession session, Model model, @PathVariable Long id,
                                 @RequestParam(name = "ep", required = false, defaultValue = "1") int episode
    ) {
        Media media = mediaRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("media", media);
        model.addAttribute("episode", episode);
        List<Media> mediaList = mediaRepo.findAll();
        model.addAttribute("mediaList", mediaList);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }
        return "mediaVideo";
    }
}
