package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.AdminProblemResponse;
import Competitive.Programming.Tracker.dto.AdminStatsResponse;
import Competitive.Programming.Tracker.dto.AdminUserResponse;
import Competitive.Programming.Tracker.entity.Role;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.GoalRepository;
import Competitive.Programming.Tracker.repository.NotificationRepository;
import Competitive.Programming.Tracker.repository.PlannerTaskRepository;
import Competitive.Programming.Tracker.repository.PlatformAccountRepository;
import Competitive.Programming.Tracker.repository.ProblemRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final PlatformAccountRepository platformAccountRepository;
    private final GoalRepository goalRepository;
    private final PlannerTaskRepository plannerTaskRepository;
    private final NotificationRepository notificationRepository;

    public AdminService(
            UserRepository userRepository,
            ProblemRepository problemRepository,
            PlatformAccountRepository platformAccountRepository,
            GoalRepository goalRepository,
            PlannerTaskRepository plannerTaskRepository,
            NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.platformAccountRepository = platformAccountRepository;
        this.goalRepository = goalRepository;
        this.plannerTaskRepository = plannerTaskRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(user -> new AdminUserResponse(
                        user,
                        problemRepository.findByUser(user).size(),
                        platformAccountRepository.findByUserOrderByIdAsc(user).size(),
                        goalRepository.findByUserOrderByEndDateAscIdAsc(user).size(),
                        plannerTaskRepository.findByUserOrderByTaskDateAscCreatedAtAsc(user).size()))
                .toList();
    }

    @Transactional
    public AdminUserResponse setUserEnabled(Long id, boolean enabled, String currentUsername) {
        User user = getUser(id);
        if (user.getUsername().equals(currentUsername) && !enabled) {
            throw new IllegalStateException("You cannot disable your own admin account");
        }
        user.setEnabled(enabled);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public AdminUserResponse setUserRole(Long id, String role, String currentUsername) {
        User user = getUser(id);

        final Role requestedRole;
        try {
            requestedRole = Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role. Use USER or ADMIN");
        }

        if (user.getUsername().equals(currentUsername) && requestedRole != Role.ADMIN) {
            throw new IllegalStateException("You cannot remove your own admin role");
        }

        user.setRole(requestedRole);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public void deleteUser(Long id, String currentUsername) {
        User user = getUser(id);
        if (user.getUsername().equals(currentUsername)) {
            throw new IllegalStateException("You cannot delete your own admin account");
        }

        // Remove dependent records first so this operation works without
        // requiring database-level cascade rules.
        plannerTaskRepository.deleteByUser(user);
        platformAccountRepository.deleteByUser(user);
        problemRepository.deleteByUser(user);
        notificationRepository.deleteByUser(user);
        goalRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    @Transactional
    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new RuntimeException("Problem not found");
        }
        problemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AdminProblemResponse> getProblems() {
        return problemRepository.findAll()
                .stream()
                .map(AdminProblemResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabled(true);
        long disabledUsers = userRepository.countByEnabled(false);
        long adminUsers = userRepository.countByRole(Role.ADMIN);
        long totalProblems = problemRepository.count();
        long totalPlatformAccounts = platformAccountRepository.count();
        long totalGoals = goalRepository.count();
        long totalPlannerTasks = plannerTaskRepository.count();
        long completedPlannerTasks = plannerTaskRepository.countByCompleted(true);
        long totalNotifications = notificationRepository.count();

        return new AdminStatsResponse(
                totalUsers,
                activeUsers,
                disabledUsers,
                adminUsers,
                totalProblems,
                totalPlatformAccounts,
                totalGoals,
                totalPlannerTasks,
                completedPlannerTasks,
                totalNotifications);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user,
                problemRepository.findByUser(user).size(),
                platformAccountRepository.findByUserOrderByIdAsc(user).size(),
                goalRepository.findByUserOrderByEndDateAscIdAsc(user).size(),
                plannerTaskRepository.findByUserOrderByTaskDateAscCreatedAtAsc(user).size());
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
