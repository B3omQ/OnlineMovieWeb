package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.GenreRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private MediaRepo mediaRepository;

    @Autowired
    private GenreRepo genreRepo;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Media> mustWatch = mediaRepository.findTop5ByOrderByViewsDesc();
        List<Media> latestMedia = mediaRepository.findTop5ByOrderByReleaseYearDesc();
        List<Genre> topGenres = genreRepo.findTop5MostUsedGenres(PageRequest.of(0, 5));
        model.addAttribute("mustWatch", mustWatch);
        model.addAttribute("latestMedia", latestMedia);
        model.addAttribute("genres", topGenres);

        return "home";
    }

    @GetMapping("/genres/popular")
    public String showPopularGenres(Model model) {
        List<Genre> topGenres = genreRepo.findTop5MostUsedGenres(PageRequest.of(0, 5));
        model.addAttribute("genres", topGenres);
        return "genre-list";
    }

    // HomeController.java
    @GetMapping("/lastestrelease")
    public String showAllRelease(Model model) {
        List<Media> allMedia = mediaRepository.findAll();
        allMedia.sort(Comparator.comparing(Media::getReleaseYear).reversed());
        model.addAttribute("allMedia", allMedia);
        return "lastestrelease";
    }

    // HomeController.java
    @GetMapping("/movies")
    public String showAllMovies(Model model) {
        List<Media> allMedia = mediaRepository.findAll();
        allMedia.sort(Comparator.comparing(Media::getReleaseYear).reversed());
        model.addAttribute("allMedia", allMedia);
        return "movies";
    }

    @GetMapping("/series")
    public String showAllSeries(Model model) {
        List<Media> allMedia = mediaRepository.findAll();
        allMedia.sort(Comparator.comparing(Media::getReleaseYear).reversed());
        model.addAttribute("allMedia", allMedia);
        return "series";
    }

//    @GetMapping("/genres")
//    public String showAllGenres(Model model) {
//        List<Genre> allGenres = genreRepo.findAll();
//        model.addAttribute("allGenres", allGenres);
//        return "genre";
//    }


}
