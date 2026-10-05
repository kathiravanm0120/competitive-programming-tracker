package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.PlannerTaskRequest;
import Competitive.Programming.Tracker.dto.PlannerTaskResponse;
import Competitive.Programming.Tracker.service.PlannerTaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planner/tasks")
public class PlannerTaskController {

    private final PlannerTaskService plannerTaskService;

    public PlannerTaskController(PlannerTaskService plannerTaskService) {
        this.plannerTaskService = plannerTaskService;
    }

    @GetMapping
    public ResponseEntity<List<PlannerTaskResponse>> getTasks(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Authentication authentication) {
        return ResponseEntity.ok(
                plannerTaskService.getTasks(authentication.getName(), date)
        );
    }

    @PostMapping
    public ResponseEntity<PlannerTaskResponse> createTask(
            @RequestBody PlannerTaskRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                plannerTaskService.createTask(request, authentication.getName())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlannerTaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody PlannerTaskRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                plannerTaskService.updateTask(id, request, authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        plannerTaskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
