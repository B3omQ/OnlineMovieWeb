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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MediaRepo mediaRepository;

    @Autowired
    private GenreRepo genreRepo;

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

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
    public String showAllRelease(Model model, HttpSession session) {
        List<Media> allMedia = mediaRepository.findAll();
        allMedia.sort(Comparator.comparing(Media::getReleaseYear).reversed());
        model.addAttribute("allMedia", allMedia);
        model.addAttribute("pageTitle", "Latest Release");
        model.addAttribute("sectionTitle", "Latest Release");
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "seperated_film";
    }

    @GetMapping("/movies")
    public String showAllMovies(Model model, HttpSession session) {
        List<Media> allMedia = mediaRepository.findAllByType("movie");
        allMedia.sort(Comparator.comparing(Media::getReleaseYear).reversed());
        model.addAttribute("allMedia", allMedia);
        model.addAttribute("pageTitle", "All Movies");
        model.addAttribute("sectionTitle", "All Movies");
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "seperated_film";
    }

    @GetMapping("/series")
    public String showAllSeries(Model model, HttpSession session) {
        List<Media> allMedia = mediaRepository.findAllByType("Tv Show");
        allMedia.sort(Comparator.comparing(Media::getReleaseYear).reversed());
        model.addAttribute("allMedia", allMedia);
        model.addAttribute("pageTitle", "All Series");
        model.addAttribute("sectionTitle", "All Series");
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "seperated_film";
    }

    @GetMapping("/api/suggestions")
    @ResponseBody
    public List<Map<String, String>> getSuggestions(@RequestParam("q") String query) {
        String normalizedQuery = Normalizer.normalize(query, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        List<Media> suggestions = mediaRepository.findTop5ByTitleContainingIgnoreCase(normalizedQuery);

        return suggestions.stream()
                .map(media -> Map.of(
                        "title", media.getTitle(),
                        "poster", media.getPoster() != null ? media.getPoster() : ""
                ))
                .collect(Collectors.toList());
    }


    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model, HttpSession session) {
        List<Media> results = mediaRepository.findByTitleContainingIgnoreCase(query);

        model.addAttribute("allMedia", results); // Match expected attribute in seperated_film.html
        model.addAttribute("sectionTitle", "Search results for \"" + query + "\"");
        model.addAttribute("pageTitle", "Search");

        Object user = session.getAttribute("user");
        model.addAttribute("user", user);

        return "seperated_film";
    }


}
