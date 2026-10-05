package Competitive.Programming.Tracker.dto;

import java.time.LocalDate;

public class GoalRequest {

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

    public GoalRequest() {
    }

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

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setPlatform(String platform) { this.platform = platform; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setStatus(String status) { this.status = status; }
}
