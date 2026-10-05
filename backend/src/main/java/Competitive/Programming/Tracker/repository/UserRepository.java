package Competitive.Programming.Tracker.repository;

import Competitive.Programming.Tracker.entity.Role;
import Competitive.Programming.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByEnabled(boolean enabled);
    long countByRole(Role role);
    List<User> findAllByOrderByCreatedAtDesc();
}
