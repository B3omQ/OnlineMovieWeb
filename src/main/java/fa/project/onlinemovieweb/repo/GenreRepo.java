package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GenreRepo extends JpaRepository<Genre, Long> {
    Genre findByName(String name);

    @Query("SELECT g FROM Genre g JOIN g.mediaList m GROUP BY g ORDER BY COUNT(m.id) DESC")
    List<Genre> findTop5MostUsedGenres(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT g.name FROM Genre g ORDER BY g.name")
    List<String> findAllGenreNames();


}
