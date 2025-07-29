package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Favorite;
import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepo extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndMedia(User user, Media media);

    void deleteByMediaIdAndUserId(Long id, Long id1);
}
