package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepo extends JpaRepository<Genre, Integer> {
    Genre findByName(String name);
}
