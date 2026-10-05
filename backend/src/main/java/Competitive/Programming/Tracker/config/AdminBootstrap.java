package Competitive.Programming.Tracker.config;

import Competitive.Programming.Tracker.entity.Role;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminBootstrap {

    @Bean
    CommandLineRunner promoteConfiguredAdmin(
            UserRepository userRepository,
            @Value("${app.admin.bootstrap-username:}") String bootstrapUsername) {

        return args -> {
            if (bootstrapUsername == null || bootstrapUsername.isBlank()) {
                return;
            }

            userRepository.findByUsername(bootstrapUsername.trim())
                    .ifPresent(user -> {
                        if (user.getRole() != Role.ADMIN) {
                            user.setRole(Role.ADMIN);
                            userRepository.save(user);
                        }
                    });
        };
    }
}
