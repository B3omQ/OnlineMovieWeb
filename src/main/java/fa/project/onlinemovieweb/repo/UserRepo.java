package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findByRole(Role role);

    @Query("select u from User u where u.username like %:query% or u.email like %:query%")
    List<User> filterByQuery(String query);
}
