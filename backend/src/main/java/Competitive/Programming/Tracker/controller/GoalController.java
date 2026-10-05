package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.GoalRequest;
import Competitive.Programming.Tracker.dto.GoalResponse;
import Competitive.Programming.Tracker.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getGoals(
            Authentication authentication) {
        return ResponseEntity.ok(
                goalService.getGoals(authentication.getName())
        );
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @RequestBody GoalRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                goalService.createGoal(request, authentication.getName())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @RequestBody GoalRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                goalService.updateGoal(id, request, authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            Authentication authentication) {
        goalService.deleteGoal(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
