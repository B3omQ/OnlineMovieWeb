package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.repo.GenreRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;

@Controller
public class GenreController {
    @Autowired
    GenreRepo genreRepo;

    @Autowired
    MediaRepo mediaRepo;

    @GetMapping("/genres")
    public String showGenres(HttpSession session, Model model) {
        List<Genre> genres = genreRepo.findAll();
        genres.sort(Comparator.comparing(Genre::getName, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute("genres", genres);
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "genres";
    }

    @GetMapping("/genres/{id}")
    public String showGenre(@PathVariable("id") Integer genreId, HttpSession session, Model model) {

        Genre genre = genreRepo.findById(genreId).orElse(null);
        List<Media> mediaList = mediaRepo.findByGenresContaining(genre);
        model.addAttribute("genre", genre);
        model.addAttribute("allMedia", mediaList);
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", genre.getName());
        model.addAttribute("sectionTitle", genre.getName());
        return "seperated_film";
    }


}
