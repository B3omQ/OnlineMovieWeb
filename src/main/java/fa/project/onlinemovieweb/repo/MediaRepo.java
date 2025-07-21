package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MediaRepo extends JpaRepository<Media, Long> {
    List<Media> findTop5ByOrderByViewsDesc();

    List<Media> findTop5ByOrderByReleaseYearDesc();

    List<Media> findAllByType(String type);

    List<Media> findByGenresContaining(Genre genre);

    List<Media> findByTitleContainingIgnoreCase(String title);

    List<Media> findTop5ByTitleContainingIgnoreCase(String title);

}


