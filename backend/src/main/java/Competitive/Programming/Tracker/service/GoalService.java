package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.GoalRequest;
import Competitive.Programming.Tracker.dto.GoalResponse;
import Competitive.Programming.Tracker.entity.Goal;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.GoalRepository;
import Competitive.Programming.Tracker.repository.PlannerTaskRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final PlannerTaskRepository plannerTaskRepository;
    private final NotificationService notificationService;

    public GoalService(
            GoalRepository goalRepository,
            UserRepository userRepository,
            PlannerTaskRepository plannerTaskRepository,
            NotificationService notificationService) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.plannerTaskRepository = plannerTaskRepository;
        this.notificationService = notificationService;
    }

    public List<GoalResponse> getGoals(String username) {
        User user = getUser(username);
        return goalRepository.findByUserOrderByEndDateAscIdAsc(user)
                .stream()
                .map(GoalResponse::new)
                .toList();
    }

    public GoalResponse createGoal(GoalRequest request, String username) {
        validate(request);

        User user = getUser(username);
        Goal goal = new Goal();
        apply(goal, request);
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);
        notifyGoalProgress(user, savedGoal);
        return new GoalResponse(savedGoal);
    }

    public GoalResponse updateGoal(
            Long id,
            GoalRequest request,
            String username) {

        validate(request);

        User user = getUser(username);
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        apply(goal, request);
        Goal savedGoal = goalRepository.save(goal);
        notifyGoalProgress(user, savedGoal);
        return new GoalResponse(savedGoal);
    }

    public void deleteGoal(Long id, String username) {
        User user = getUser(username);
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        plannerTaskRepository.findByGoal(goal)
                .forEach(task -> task.setGoal(null));
        plannerTaskRepository.flush();
        goalRepository.delete(goal);
    }

    private void notifyGoalProgress(User user, Goal goal) {
        if (goal.getTargetValue() == null || goal.getTargetValue() <= 0) {
            return;
        }

        int current = goal.getCurrentValue() == null ? 0 : goal.getCurrentValue();
        int target = goal.getTargetValue();
        int progress = (int) Math.floor((current * 100.0) / target);

        if (current >= target) {
            notificationService.createGoalNotification(
                    user,
                    goal.getId(),
                    "Goal Completed",
                    goal.getTitle() + " has reached its target.",
                    "COMPLETED"
            );
        } else if (progress >= 80) {
            notificationService.createGoalNotification(
                    user,
                    goal.getId(),
                    "Goal Almost Complete",
                    goal.getTitle() + " is " + progress + "% complete.",
                    "80_PERCENT"
            );
        }
    }

    private void apply(Goal goal, GoalRequest request) {
        goal.setTitle(request.getTitle().trim());
        goal.setDescription(normalize(request.getDescription()));
        goal.setGoalType(normalize(request.getGoalType()) == null
                ? "CUSTOM"
                : request.getGoalType().trim().toUpperCase());
        goal.setTargetValue(request.getTargetValue());
        goal.setCurrentValue(request.getCurrentValue() == null
                ? 0
                : Math.max(0, request.getCurrentValue()));
        goal.setUnit(normalize(request.getUnit()));
        goal.setStartDate(request.getStartDate());
        goal.setEndDate(request.getEndDate());
        goal.setPlatform(normalize(request.getPlatform()));
        goal.setTopic(normalize(request.getTopic()));

        String requestedStatus = normalize(request.getStatus());
        if (requestedStatus == null) {
            requestedStatus = "ACTIVE";
        }
        requestedStatus = requestedStatus.toUpperCase();
        if (!List.of("ACTIVE", "PAUSED", "CANCELLED").contains(requestedStatus)) {
            requestedStatus = "ACTIVE";
        }
        goal.setStatus(requestedStatus);
    }

    private void validate(GoalRequest request) {
        if (request == null || !StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("Goal title is required");
        }
        if (request.getTargetValue() == null || request.getTargetValue() <= 0) {
            throw new IllegalArgumentException("Target value must be greater than 0");
        }
        if (request.getCurrentValue() != null && request.getCurrentValue() < 0) {
            throw new IllegalArgumentException("Current value cannot be negative");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
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
