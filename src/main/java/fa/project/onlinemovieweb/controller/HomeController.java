package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.GenreRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private MediaRepo mediaRepository;
    @Autowired
    private GenreRepo genreRepo;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Add logged-in username if exists
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }

        // Add media data
        model.addAttribute("mustWatch", mediaRepository.findTop5ByOrderByViewsDesc());
        model.addAttribute("latestMedia", mediaRepository.findTop5ByOrderByReleaseYearDesc());
        model.addAttribute("genres", genreRepo.findAll());

        return "home";
    }
    @GetMapping("/genre/{id}")
    public String moviesByGenre(@PathVariable Long id, Model model) {
        Genre genre = genreRepo.findById(id).orElseThrow();
        List<Media> mediaList = mediaRepository.findByGenre(genre);

        model.addAttribute("genre", genre);
        model.addAttribute("mediaList", mediaList);

        return "genre-media"; // Create genre-media.html template
    }

}
