package fa.project.onlinemovieweb.controller;

import fa.project.onlinemovieweb.entities.*;
import fa.project.onlinemovieweb.repo.FavoriteRepo;
import fa.project.onlinemovieweb.repo.GenreRepo;
import fa.project.onlinemovieweb.repo.MediaRepo;
import fa.project.onlinemovieweb.repo.NotificationRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MediaRepo mediaRepository;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private NotificationRepo notificationRepo;

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
        List<Media> favoriteMedia = mediaRepository.findAllByFavoritesAndUser(user);
        model.addAttribute("favoriteMedia", favoriteMedia);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
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
    public String showAllRelease(@RequestParam(required = false) Integer year,
                                 @RequestParam(defaultValue = "1") int page,
                                 Model model, HttpSession session) {
        int pageSize = 10;
        Page<Media> mediaPage;

        if (year != null && year != 0) {
            mediaPage = mediaRepository.findByReleaseYear(year, PageRequest.of(page - 1, pageSize, Sort.by("releaseYear").descending()));
        } else {
            mediaPage = mediaRepository.findAll(PageRequest.of(page - 1, pageSize, Sort.by("releaseYear").descending()));
        }

        model.addAttribute("allMedia", mediaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mediaPage.getTotalPages());
        model.addAttribute("pageTitle", "Latest Release");
        model.addAttribute("sectionTitle", "Latest Release");

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "seperated_film";
    }

    @GetMapping("/movies")
    public String showAllMovies(@RequestParam(defaultValue = "1") int page,
                                Model model, HttpSession session) {
        int pageSize = 10;
        Page<Media> mediaPage = mediaRepository.findByType("movie", PageRequest.of(page - 1, pageSize, Sort.by("releaseYear").descending()));

        model.addAttribute("allMedia", mediaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mediaPage.getTotalPages());
        model.addAttribute("pageTitle", "All Movies");
        model.addAttribute("sectionTitle", "All Movies");

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "seperated_film";
    }

    @GetMapping("/series")
    public String showAllSeries(@RequestParam(defaultValue = "1") int page,
                                Model model, HttpSession session) {
        int pageSize = 10;
        Page<Media> mediaPage = mediaRepository.findByType("Tv Show", PageRequest.of(page - 1, pageSize, Sort.by("releaseYear").descending()));

        model.addAttribute("allMedia", mediaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mediaPage.getTotalPages());
        model.addAttribute("pageTitle", "All Series");
        model.addAttribute("sectionTitle", "All Series");

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
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


    @GetMapping("/advanced_search")
    public String showAdvancedSearch(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) List<Integer> year, // changed to List
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "1") int page,
            Model model,
            HttpSession session) {

        int pageSize = 10;

        // Sorting logic
        Sort sorting = switch (sort.toLowerCase()) {
            case "oldest" -> Sort.by("releaseYear").ascending();
            case "most" -> Sort.by("views").descending();
            case "least" -> Sort.by("views").ascending();
            default -> Sort.by("releaseYear").descending();
        };


        Pageable pageable = PageRequest.of(page - 1, pageSize, sorting);

        // Convert comma-separated strings to lists
        List<String> languageList = (language != null && !language.isEmpty())
                ? Arrays.asList(language.split(","))
                : null;

        List<String> typeList = (type != null && !type.isEmpty())
                ? Arrays.asList(type.split(","))
                : null;

        List<String> genreList = (genre != null && !genre.isEmpty())
                ? Arrays.asList(genre.split(","))
                : null;

        // Use 'year' directly since it's already a List<Integer>
        List<Integer> yearList = (year != null && !year.isEmpty()) ? year : null;

        // Query database
        Page<Media> mediaPage = mediaRepository.findWithFilters(languageList, typeList, genreList, yearList, pageable);

        // Model attributes
        model.addAttribute("allMedia", mediaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mediaPage.getTotalPages());
        model.addAttribute("pageTitle", "Advanced Search");
        model.addAttribute("sectionTitle", "Advanced Search");

        // Filters
        model.addAttribute("languages", mediaRepository.findDistinctLanguages());
        model.addAttribute("types", List.of("Movie", "TV Show"));
        model.addAttribute("genres", genreRepo.findAllGenreNames());
        model.addAttribute("years", mediaRepository.findDistinctYears());

        // Preserve selection
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedLanguage", language);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("noResults", mediaPage.getContent().isEmpty());
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "advanced_search";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query,
                         @RequestParam(defaultValue = "1") int page,
                         Model model, HttpSession session) {

        int pageSize = 10;
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by("releaseYear").descending());
        Page<Media> resultsPage = mediaRepository.findByTitleContainingIgnoreCase(query, pageable);

        model.addAttribute("allMedia", resultsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resultsPage.getTotalPages());
        model.addAttribute("sectionTitle", "Search results for \"" + query + "\"");
        model.addAttribute("pageTitle", "Search");
        model.addAttribute("searchQuery", query); // Needed for pagination links
        model.addAttribute("searchQuery", query);
        model.addAttribute("noResults", resultsPage.getContent().isEmpty());
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Notification> notifications = notificationRepo.findTop5ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);

        return "seperated_film";
    }

}
