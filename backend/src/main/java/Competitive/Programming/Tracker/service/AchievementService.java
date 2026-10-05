package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.AchievementResponse;
import Competitive.Programming.Tracker.entity.Goal;
import Competitive.Programming.Tracker.entity.PlannerTask;
import Competitive.Programming.Tracker.entity.PlatformAccount;
import Competitive.Programming.Tracker.entity.Problem;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.GoalRepository;
import Competitive.Programming.Tracker.repository.PlannerTaskRepository;
import Competitive.Programming.Tracker.repository.PlatformAccountRepository;
import Competitive.Programming.Tracker.repository.ProblemRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import Competitive.Programming.Tracker.dto.CodeforcesUserResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AchievementService {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final GoalRepository goalRepository;
    private final PlannerTaskRepository plannerTaskRepository;
    private final PlatformAccountRepository platformAccountRepository;
    private final CodeforcesService codeforcesService;

    public AchievementService(
            UserRepository userRepository,
            ProblemRepository problemRepository,
            GoalRepository goalRepository,
            PlannerTaskRepository plannerTaskRepository,
            PlatformAccountRepository platformAccountRepository,
            CodeforcesService codeforcesService) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.goalRepository = goalRepository;
        this.plannerTaskRepository = plannerTaskRepository;
        this.platformAccountRepository = platformAccountRepository;
        this.codeforcesService = codeforcesService;
    }

    public List<AchievementResponse> getAchievements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Problem> problems = problemRepository.findByUser(user);
        List<Goal> goals = goalRepository.findByUserOrderByEndDateAscIdAsc(user);
        List<PlannerTask> tasks = plannerTaskRepository.findByUserOrderByTaskDateDescIdDesc(user);
        List<PlatformAccount> accounts =
                platformAccountRepository.findByUserOrderByIdAsc(user);

        int solvedProblems = (int) problems.stream()
                .filter(problem -> "SOLVED".equalsIgnoreCase(safe(problem.getStatus())))
                .count();

        int completedGoals = (int) goals.stream()
                .filter(goal -> goal.getTargetValue() != null
                        && goal.getCurrentValue() != null
                        && goal.getCurrentValue() >= goal.getTargetValue())
                .count();

        int completedTasks = (int) tasks.stream()
                .filter(PlannerTask::isCompleted)
                .count();

        int completedStreak = calculateConsecutiveCompletedDays(tasks);
        int connectedPlatforms = (int) accounts.stream()
                .map(PlatformAccount::getPlatform)
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .count();

        int codeforcesRating = getCodeforcesRating(accounts);

        List<AchievementResponse> result = new ArrayList<>();

        add(result, "FIRST_PROBLEM", "First Problem",
                "Solve your first tracked problem.", "Problems", "🧩",
                solvedProblems, 1);

        add(result, "SOLVED_100", "Century",
                "Solve 100 tracked problems.", "Problems", "💯",
                solvedProblems, 100);

        add(result, "SOLVED_500", "Problem Machine",
                "Solve 500 tracked problems.", "Problems", "🚀",
                solvedProblems, 500);

        add(result, "FIRST_GOAL", "Goal Crusher",
                "Complete your first goal.", "Goals", "🎯",
                completedGoals, 1);

        add(result, "PLANNER_10", "Planner Starter",
                "Complete 10 daily planner tasks.", "Planner", "📅",
                completedTasks, 10);

        add(result, "STREAK_7", "7-Day Streak",
                "Complete planner tasks on 7 consecutive days.", "Planner", "🔥",
                completedStreak, 7);

        add(result, "PLATFORMS_4", "Four Corners",
                "Connect Codeforces, LeetCode, CodeChef and AtCoder.",
                "Platforms", "🌐", connectedPlatforms, 4);

        add(result, "CF_1000", "Codeforces 1000",
                "Reach a Codeforces rating of 1000.", "Codeforces", "⭐",
                codeforcesRating, 1000);

        add(result, "CF_1500", "Codeforces 1500",
                "Reach a Codeforces rating of 1500.", "Codeforces", "🏅",
                codeforcesRating, 1500);

        add(result, "CF_2000", "Codeforces 2000",
                "Reach a Codeforces rating of 2000.", "Codeforces", "👑",
                codeforcesRating, 2000);

        return result;
    }

    private void add(
            List<AchievementResponse> result,
            String code,
            String title,
            String description,
            String category,
            String icon,
            int current,
            int target) {
        result.add(new AchievementResponse(
                code, title, description, category, icon,
                Math.max(0, current), target, current >= target));
    }

    private int getCodeforcesRating(List<PlatformAccount> accounts) {
        return accounts.stream()
                .filter(account -> "Codeforces".equalsIgnoreCase(
                        safe(account.getPlatform())))
                .findFirst()
                .map(PlatformAccount::getHandle)
                .map(handle -> {
                    try {
                        CodeforcesUserResponse profile = codeforcesService.getUser(handle);
                        return profile.getRating() == null ? 0 : profile.getRating();
                    } catch (RuntimeException ignored) {
                        return 0;
                    }
                })
                .orElse(0);
    }

    private int calculateConsecutiveCompletedDays(List<PlannerTask> tasks) {
        Set<LocalDate> completedDates = new HashSet<>();

        for (PlannerTask task : tasks) {
            if (task.isCompleted() && task.getTaskDate() != null) {
                completedDates.add(task.getTaskDate());
            }
        }

        if (completedDates.isEmpty()) {
            return 0;
        }

        LocalDate start = completedDates.stream()
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        int streak = 0;
        LocalDate cursor = start;

        while (completedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
