package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);
    List<User> findAllByUsernameIgnoreCase(String username);
    List<User> findByUsername(String username);
}
