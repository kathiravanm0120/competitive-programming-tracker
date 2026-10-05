package Competitive.Programming.Tracker.dto;

import java.time.LocalDate;

public class PlannerTaskRequest {

    private String title;
    private String description;
    private LocalDate taskDate;
    private String priority;
    private boolean completed;
    private Long goalId;

    public PlannerTaskRequest() {
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getTaskDate() { return taskDate; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public Long getGoalId() { return goalId; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setTaskDate(LocalDate taskDate) { this.taskDate = taskDate; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }
}
