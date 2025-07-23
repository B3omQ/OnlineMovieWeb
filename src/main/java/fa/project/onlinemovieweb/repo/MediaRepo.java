package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MediaRepo extends JpaRepository<Media, Long> {
    List<Media> findTop5ByOrderByViewsDesc();

    List<Media> findTop5ByOrderByReleaseYearDesc();

    List<Media> findAllByType(String type);

    List<Media> findByGenresContaining(Genre genre);

    List<Media> findByTitleContainingIgnoreCase(String title);

    List<Media> findTop5ByTitleContainingIgnoreCase(String title);

    List<Media> findByReleaseYear(int year);

    @Query("SELECT DISTINCT m.releaseYear FROM Media m ORDER BY m.releaseYear DESC")
    List<Integer> findDistinctYears();

    Page<Media> findByReleaseYear(Integer year, Pageable pageable);

    Page<Media> findByType(String type, Pageable pageable);

    Page<Media> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Media> findByGenresContaining(Genre genre, Pageable pageable);

}


