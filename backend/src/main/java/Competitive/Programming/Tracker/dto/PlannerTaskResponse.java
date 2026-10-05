package Competitive.Programming.Tracker.dto;

import Competitive.Programming.Tracker.entity.PlannerTask;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlannerTaskResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate taskDate;
    private String priority;
    private boolean completed;
    private LocalDateTime completedAt;
    private Long goalId;
    private String goalTitle;

    public PlannerTaskResponse(PlannerTask task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.taskDate = task.getTaskDate();
        this.priority = task.getPriority();
        this.completed = task.isCompleted();
        this.completedAt = task.getCompletedAt();
        if (task.getGoal() != null) {
            this.goalId = task.getGoal().getId();
            this.goalTitle = task.getGoal().getTitle();
        }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getTaskDate() { return taskDate; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Long getGoalId() { return goalId; }
    public String getGoalTitle() { return goalTitle; }
}
