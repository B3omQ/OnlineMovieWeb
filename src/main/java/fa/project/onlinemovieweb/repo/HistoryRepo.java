package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Episode;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.entities.WatchHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryRepo extends JpaRepository<WatchHistory, Long> {
    @EntityGraph(attributePaths = {"media", "media.genres"})
    List<WatchHistory> findByUserOrderByWatchedAtDesc(User user);

    List<WatchHistory> findByUser(User user);

    List<WatchHistory> findByUserAndMedia(User user, Media media);

    List<WatchHistory> findByUserAndEpisode(User user, Episode episode);

    Optional<WatchHistory> findFirstByUserAndMedia(User user, Media media);

    Optional<WatchHistory> findFirstByUserAndEpisode(User user, Episode episode);

    List<WatchHistory> findByWatchedAtAfter(LocalDateTime dateTime);

    void deleteByUser(User user);

    void deleteByUserAndMedia(User user, Media media);

    long countByMedia(Media media);

    long countByEpisode(Episode episode);
}
