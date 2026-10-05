package Competitive.Programming.Tracker.repository;

import Competitive.Programming.Tracker.entity.Problem;
import Competitive.Programming.Tracker.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository
        extends JpaRepository<Problem, Long> {

    List<Problem> findByUser(User user);


    long deleteByUser(User user);

}