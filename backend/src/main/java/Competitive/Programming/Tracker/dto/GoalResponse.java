package Competitive.Programming.Tracker.dto;

import Competitive.Programming.Tracker.entity.Goal;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoalResponse {

    private Long id;
    private String title;
    private String description;
    private String goalType;
    private Integer targetValue;
    private Integer currentValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;
    private String platform;
    private String topic;
    private String status;
    private int progressPercent;
    private boolean overdue;
    private LocalDateTime createdAt;

    public GoalResponse(Goal goal) {
        this.id = goal.getId();
        this.title = goal.getTitle();
        this.description = goal.getDescription();
        this.goalType = goal.getGoalType();
        this.targetValue = goal.getTargetValue();
        this.currentValue = goal.getCurrentValue();
        this.unit = goal.getUnit();
        this.startDate = goal.getStartDate();
        this.endDate = goal.getEndDate();
        this.platform = goal.getPlatform();
        this.topic = goal.getTopic();
        this.status = effectiveStatus(goal);
        this.progressPercent = calculateProgress(goal.getCurrentValue(), goal.getTargetValue());
        this.overdue = "OVERDUE".equals(this.status);
        this.createdAt = goal.getCreatedAt();
    }

    private String effectiveStatus(Goal goal) {
        if (goal.getCurrentValue() != null
                && goal.getTargetValue() != null
                && goal.getCurrentValue() >= goal.getTargetValue()) {
            return "COMPLETED";
        }

        if ("PAUSED".equalsIgnoreCase(goal.getStatus())) {
            return "PAUSED";
        }

        if ("CANCELLED".equalsIgnoreCase(goal.getStatus())) {
            return "CANCELLED";
        }

        if (goal.getEndDate() != null && goal.getEndDate().isBefore(LocalDate.now())) {
            return "OVERDUE";
        }

        return "ACTIVE";
    }

    private int calculateProgress(Integer current, Integer target) {
        if (target == null || target <= 0 || current == null) {
            return 0;
        }
        return Math.min(100, Math.max(0, (int) Math.round((current * 100.0) / target)));
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getGoalType() { return goalType; }
    public Integer getTargetValue() { return targetValue; }
    public Integer getCurrentValue() { return currentValue; }
    public String getUnit() { return unit; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getPlatform() { return platform; }
    public String getTopic() { return topic; }
    public String getStatus() { return status; }
    public int getProgressPercent() { return progressPercent; }
    public boolean isOverdue() { return overdue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
