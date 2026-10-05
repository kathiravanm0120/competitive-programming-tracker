package Competitive.Programming.Tracker.repository;

import Competitive.Programming.Tracker.entity.Goal;
import Competitive.Programming.Tracker.entity.PlannerTask;
import Competitive.Programming.Tracker.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlannerTaskRepository extends JpaRepository<PlannerTask, Long> {

    /*
     * ============================================================
     * USER TASK QUERIES
     * ============================================================
     */

    // Ascending date + ID
    List<PlannerTask> findByUserOrderByTaskDateAscIdAsc(User user);

    // Descending date + ID
    List<PlannerTask> findByUserOrderByTaskDateDescIdDesc(User user);

    // Tasks for a specific date
    List<PlannerTask> findByUserAndTaskDateOrderByCompletedAscIdAsc(
            User user,
            LocalDate taskDate);

    /*
     * ============================================================
     * SINGLE TASK QUERIES
     * ============================================================
     */

    Optional<PlannerTask> findByIdAndUser(
            Long id,
            User user);

    /*
     * ============================================================
     * GOAL QUERIES
     * ============================================================
     */

    List<PlannerTask> findByGoal(Goal goal);

    /*
     * ============================================================
     * DELETE / COUNT QUERIES
     * ============================================================
     */

    void deleteByUser(User user);

    long countByCompleted(boolean completed);

    /*
     * ============================================================
     * BACKWARD-COMPATIBILITY METHOD
     * ============================================================
     *
     * Some existing services still call:
     *
     * findByUserOrderByTaskDateAscCreatedAtAsc(...)
     *
     * PlannerTask does not have a createdAt field, so Spring Data
     * cannot derive that query.
     *
     * Keep the old method name as a default method and delegate to
     * the valid taskDate + id ordering.
     */

    default List<PlannerTask> findByUserOrderByTaskDateAscCreatedAtAsc(
            User user) {
        return findByUserOrderByTaskDateAscIdAsc(user);
    }
}