package Competitive.Programming.Tracker.dto;

import Competitive.Programming.Tracker.entity.User;
import java.time.LocalDateTime;

public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private long problemCount;
    private long platformAccountCount;
    private long goalCount;
    private long plannerTaskCount;

    public AdminUserResponse(
            User user,
            long problemCount,
            long platformAccountCount,
            long goalCount,
            long plannerTaskCount) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
        this.problemCount = problemCount;
        this.platformAccountCount = platformAccountCount;
        this.goalCount = goalCount;
        this.plannerTaskCount = plannerTaskCount;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getProblemCount() { return problemCount; }
    public long getPlatformAccountCount() { return platformAccountCount; }
    public long getGoalCount() { return goalCount; }
    public long getPlannerTaskCount() { return plannerTaskCount; }
}
