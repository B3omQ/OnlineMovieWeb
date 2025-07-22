package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Favorite;
import fa.project.onlinemovieweb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepo extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);
}
