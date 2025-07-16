package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByUsername(String username);
}
