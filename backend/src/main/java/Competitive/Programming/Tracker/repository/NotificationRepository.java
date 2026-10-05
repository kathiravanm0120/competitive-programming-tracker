package Competitive.Programming.Tracker.repository;

import Competitive.Programming.Tracker.entity.Notification;
import Competitive.Programming.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop50ByUserOrderByCreatedAtDescIdDesc(User user);

    Optional<Notification> findByIdAndUser(Long id, User user);

    long countByUserAndReadFalse(User user);

    List<Notification> findByUserAndReadFalse(User user);

    boolean existsByUserAndReferenceKey(User user, String referenceKey);

    long deleteByUser(User user);

}
