package Competitive.Programming.Tracker.repository;

import Competitive.Programming.Tracker.entity.Goal;
import Competitive.Programming.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserOrderByEndDateAscIdAsc(User user);

    Optional<Goal> findByIdAndUser(Long id, User user);

    long deleteByUser(User user);

}
