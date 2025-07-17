package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.repo.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class GenreController {
    @Autowired
    GenreRepo genreRepo;

    @GetMapping("/genres")
    public String showGenres(Model model) {
        List<Genre> genres = genreRepo.findAll(); // or however you get genres
        genres.sort(Comparator.comparing(Genre::getName, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute("genres", genres);
        return "genres";
    }


}
