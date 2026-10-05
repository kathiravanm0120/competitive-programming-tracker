package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.ProblemRequest;
import Competitive.Programming.Tracker.dto.ProblemResponse;
import Competitive.Programming.Tracker.service.ProblemService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getProblems(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                problemService.getUserProblems(username));
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(
            @RequestBody ProblemRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                problemService.createProblem(request, username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                problemService.updateProblem(
                        id,
                        request,
                        username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        problemService.deleteProblem(id, username);

        return ResponseEntity.noContent().build();
    }
}