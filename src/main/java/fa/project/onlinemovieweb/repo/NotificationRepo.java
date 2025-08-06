package fa.project.onlinemovieweb.repo;

import fa.project.onlinemovieweb.entities.Notification;
import fa.project.onlinemovieweb.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findTop5ByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadFalse(User user);


    @Modifying
    @Transactional
    @Query("update Notification n set n.triggeredBy = null where n.triggeredBy.id = :userId")
    void clearTriggeredByUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("delete from Notification n where n.comment.id in (select c.id from Comment c where c.user.id = :userId)")
    void deleteNotificationsForUserComments(@Param("userId") Long userId);

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

}

