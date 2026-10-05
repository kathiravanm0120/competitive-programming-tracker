package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.AchievementResponse;
import Competitive.Programming.Tracker.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAchievements(
            Authentication authentication) {
        return ResponseEntity.ok(
                achievementService.getAchievements(authentication.getName())
        );
    }
}
