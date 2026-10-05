package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.AdminProblemResponse;
import Competitive.Programming.Tracker.dto.AdminStatsResponse;
import Competitive.Programming.Tracker.dto.AdminUserResponse;
import Competitive.Programming.Tracker.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> stats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> users() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<AdminUserResponse> setStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled,
            Authentication authentication) {
        return ResponseEntity.ok(adminService.setUserEnabled(id, enabled, authentication.getName()));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<AdminUserResponse> setRole(
            @PathVariable Long id,
            @RequestParam String role,
            Authentication authentication) {
        return ResponseEntity.ok(adminService.setUserRole(id, role, authentication.getName()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        adminService.deleteUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/problems")
    public ResponseEntity<List<AdminProblemResponse>> problems() {
        return ResponseEntity.ok(adminService.getProblems());
    }

    @DeleteMapping("/problems/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        adminService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
