package fa.project.onlinemovieweb.config;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.repo.GenreRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedGenres(GenreRepo genreRepo) {
        return args -> {
            List<String> genres = List.of(
                    "Action", "Adventure", "Animation", "Biography", "Comedy",
                    "Crime", "Documentary", "Drama", "Family", "Fantasy",
                    "Film-Noir", "History", "Horror", "Music", "Musical",
                    "Mystery", "News", "Reality-TV", "Romance", "Sci-Fi",
                    "Short", "Sport", "Superhero", "Talk-Show", "Thriller",
                    "War", "Western", "Game-Show", "Cooking", "Kids",
                    "Teen", "Survival", "Psychological", "Parody", "Disaster",
                    "Martial Arts", "Holiday", "Heist", "Cyberpunk", "Space", "Detective"
            );

            for (String name : genres) {
                if (genreRepo.findByName(name) == null) {
                    Genre genre = new Genre();
                    genre.setName(name);
                    genreRepo.save(genre);
                }
            }

            System.out.println("✅ Genre seeding completed.");
        };
    }
}
