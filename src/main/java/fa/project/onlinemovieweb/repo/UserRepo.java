package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Media;
import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByVerificationToken(String token);
    List<User> findAllByUsernameIgnoreCase(String username);
    List<User> findByUsername(String username);
    List<User> findByRole(Role role);

    @Query("select u from User u where u.username like %:query% or u.email like %:query%")
    List<User> filterByQuery(String query);

    @Query("select f.user from Favorite f where f.media = ?1")
    List<User> findByFavoriteMedia(Media media);
}
