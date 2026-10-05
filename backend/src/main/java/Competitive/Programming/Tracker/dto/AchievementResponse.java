package Competitive.Programming.Tracker.dto;

public class AchievementResponse {

    private String code;
    private String title;
    private String description;
    private String category;
    private String icon;
    private Integer currentValue;
    private Integer targetValue;
    private Integer progressPercent;
    private boolean unlocked;

    public AchievementResponse() {
    }

    public AchievementResponse(
            String code,
            String title,
            String description,
            String category,
            String icon,
            int currentValue,
            int targetValue,
            boolean unlocked) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.category = category;
        this.icon = icon;
        this.currentValue = Math.max(0, currentValue);
        this.targetValue = Math.max(1, targetValue);
        this.unlocked = unlocked;
        this.progressPercent = Math.min(100,
                (int) Math.round((this.currentValue * 100.0) / this.targetValue));
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getIcon() { return icon; }
    public Integer getCurrentValue() { return currentValue; }
    public Integer getTargetValue() { return targetValue; }
    public Integer getProgressPercent() { return progressPercent; }
    public boolean isUnlocked() { return unlocked; }

    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
