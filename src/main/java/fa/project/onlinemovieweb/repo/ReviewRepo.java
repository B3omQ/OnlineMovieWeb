package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByMedia(Media media);
    List<Review> findByMediaId(Long mediaId);
    List<Review> findByMediaIdAndEpisode_Id(Long mediaId, Long episodeId);
}