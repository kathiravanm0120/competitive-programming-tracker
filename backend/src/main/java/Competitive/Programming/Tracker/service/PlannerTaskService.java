package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.PlannerTaskRequest;
import Competitive.Programming.Tracker.dto.PlannerTaskResponse;
import Competitive.Programming.Tracker.entity.Goal;
import Competitive.Programming.Tracker.entity.PlannerTask;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.GoalRepository;
import Competitive.Programming.Tracker.repository.PlannerTaskRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlannerTaskService {

    private final PlannerTaskRepository plannerTaskRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PlannerTaskService(
            PlannerTaskRepository plannerTaskRepository,
            GoalRepository goalRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.plannerTaskRepository = plannerTaskRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<PlannerTaskResponse> getTasks(
            String username,
            LocalDate date) {

        User user = getUser(username);

        List<PlannerTask> tasks;
        if (date != null) {
            tasks = plannerTaskRepository
                    .findByUserAndTaskDateOrderByCompletedAscIdAsc(user, date);
        } else {
            tasks = plannerTaskRepository
                    .findByUserOrderByTaskDateDescIdDesc(user);
        }

        return tasks.stream()
                .map(PlannerTaskResponse::new)
                .toList();
    }

    public PlannerTaskResponse createTask(
            PlannerTaskRequest request,
            String username) {

        validate(request);
        User user = getUser(username);

        PlannerTask task = new PlannerTask();
        task.setUser(user);
        apply(task, request, user);

        PlannerTask savedTask = plannerTaskRepository.save(task);
        notifyCompletion(user, savedTask);
        return new PlannerTaskResponse(savedTask);
    }

    public PlannerTaskResponse updateTask(
            Long id,
            PlannerTaskRequest request,
            String username) {

        validate(request);
        User user = getUser(username);
        PlannerTask task = plannerTaskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Planner task not found"));

        boolean wasCompleted = task.isCompleted();
        apply(task, request, user);
        PlannerTask savedTask = plannerTaskRepository.save(task);
        if (!wasCompleted && savedTask.isCompleted()) {
            notifyCompletion(user, savedTask);
        }
        return new PlannerTaskResponse(savedTask);
    }

    public void deleteTask(Long id, String username) {
        User user = getUser(username);
        PlannerTask task = plannerTaskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Planner task not found"));
        plannerTaskRepository.delete(task);
    }

    private void notifyCompletion(User user, PlannerTask task) {
        if (!task.isCompleted()) {
            return;
        }

        notificationService.createPlannerNotification(
                user,
                task.getId(),
                "Planner Task Completed",
                task.getTitle() + " has been marked complete."
        );
    }

    private void apply(
            PlannerTask task,
            PlannerTaskRequest request,
            User user) {

        task.setTitle(request.getTitle().trim());
        task.setDescription(normalize(request.getDescription()));
        task.setTaskDate(request.getTaskDate());
        task.setPriority(normalizePriority(request.getPriority()));
        task.setCompleted(request.isCompleted());
        task.setCompletedAt(
                request.isCompleted() ? LocalDateTime.now() : null
        );

        if (request.getGoalId() == null) {
            task.setGoal(null);
        } else {
            Goal goal = goalRepository.findByIdAndUser(
                    request.getGoalId(),
                    user
            ).orElseThrow(() -> new RuntimeException("Goal not found"));
            task.setGoal(goal);
        }
    }

    private void validate(PlannerTaskRequest request) {
        if (request == null || !StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (request.getTaskDate() == null) {
            throw new IllegalArgumentException("Task date is required");
        }
    }

    private String normalizePriority(String priority) {
        if (!StringUtils.hasText(priority)) {
            return "MEDIUM";
        }

        String normalized = priority.trim().toUpperCase();
        if (!List.of("LOW", "MEDIUM", "HIGH").contains(normalized)) {
            return "MEDIUM";
        }
        return normalized;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
