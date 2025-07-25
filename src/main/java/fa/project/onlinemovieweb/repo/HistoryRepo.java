package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.entities.WatchHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface HistoryRepo extends JpaRepository<WatchHistory, Long> {
    @EntityGraph(attributePaths = {"media", "media.genres"})
    List<WatchHistory> findByUserOrderByWatchedAtDesc(User user);

    void deleteAllByMediaIdAndUserId(Long mediaId, Long userId);
}
