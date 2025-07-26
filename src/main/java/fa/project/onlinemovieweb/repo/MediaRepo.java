package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Genre;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MediaRepo extends JpaRepository<Media, Long> {
    List<Media> findTop5ByOrderByViewsDesc();

    List<Media> findTop5ByOrderByReleaseYearDesc();

    List<Media> findTop5ByTitleContainingIgnoreCase(String title);

    Page<Media> findByReleaseYear(Integer year, Pageable pageable);

    Page<Media> findByType(String type, Pageable pageable);

    Page<Media> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Media> findByGenresContaining(Genre genre, Pageable pageable);

    @Query("SELECT DISTINCT m.language FROM Media m WHERE m.language IS NOT NULL ORDER BY m.language")
    List<String> findDistinctLanguages();

    @Query("SELECT DISTINCT m.releaseYear FROM Media m ORDER BY m.releaseYear DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT m FROM Media m WHERE "
            + "(:language IS NULL OR m.language IN :language) AND "
            + "(:type IS NULL OR m.type IN :type) AND "
            + "(:genre IS NULL OR EXISTS (SELECT g FROM m.genres g WHERE g.name IN :genre)) AND "
            + "(:year IS NULL OR m.releaseYear IN :year)")
    Page<Media> findWithFilters(
            @Param("language") List<String> language,
            @Param("type") List<String> type,
            @Param("genre") List<String> genre,
            @Param("year") List<Integer> year,
            Pageable pageable
    );

    @Query("select m from Media m where m in (select f.media from Favorite f where f.user = ?1)")
    List<Media> findAllByFavoritesAndUser (User user);
}


