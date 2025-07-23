package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.repo.GenreRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showGenre(@PathVariable("id") Long genreId,
                            @RequestParam(defaultValue = "1") int page,
                            HttpSession session, Model model) {

        Genre genre = genreRepo.findById(genreId).orElse(null);
        if (genre == null) {
            return "redirect:/"; // or show an error page
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("releaseYear").descending());
        Page<Media> mediaPage = mediaRepo.findByGenresContaining(genre, pageable);

        model.addAttribute("genre", genre);
        model.addAttribute("allMedia", mediaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mediaPage.getTotalPages());
        model.addAttribute("pageTitle", genre.getName());
        model.addAttribute("sectionTitle", genre.getName());

        Object user = session.getAttribute("user");
        model.addAttribute("user", user);

        return "seperated_film";
    }



}
