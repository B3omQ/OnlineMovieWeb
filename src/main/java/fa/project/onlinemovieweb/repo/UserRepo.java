package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findAllByUsernameIgnoreCase(String username);
}
