package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Notification;
import fa.project.onlinemovieweb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findTop5ByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadFalse(User user);

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);
}

