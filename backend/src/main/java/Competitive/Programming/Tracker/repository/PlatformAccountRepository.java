package Competitive.Programming.Tracker.repository;

import Competitive.Programming.Tracker.entity.PlatformAccount;
import Competitive.Programming.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformAccountRepository extends JpaRepository<PlatformAccount, Long> {

    List<PlatformAccount> findByUserOrderByIdAsc(User user);

    Optional<PlatformAccount> findByUserAndPlatform(User user, String platform);

    long deleteByUser(User user);

}
