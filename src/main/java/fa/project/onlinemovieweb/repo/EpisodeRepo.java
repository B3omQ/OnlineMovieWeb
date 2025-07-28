package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepo extends JpaRepository<Episode, Long> {
    List<Episode> findByMediaIdOrderBySeasonAscEpisodeNumberAsc(Long mediaId);

}
