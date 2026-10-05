package Competitive.Programming.Tracker.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportSummaryResponse {

    private String username;
    private LocalDateTime generatedAt;
    private int trackedProblems;
    private int trackedSolvedProblems;
    private int platformsConnected;
    private int totalPlatformSolved;
    private int totalContests;
    private int goalsTotal;
    private int goalsActive;
    private int goalsCompleted;
    private int plannerTasksTotal;
    private int plannerTasksCompleted;
    private int achievementsTotal;
    private int achievementsUnlocked;
    private List<PlatformStatistics> platforms = new ArrayList<>();

    public ReportSummaryResponse() {
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public int getTrackedProblems() { return trackedProblems; }
    public void setTrackedProblems(int trackedProblems) { this.trackedProblems = trackedProblems; }
    public int getTrackedSolvedProblems() { return trackedSolvedProblems; }
    public void setTrackedSolvedProblems(int trackedSolvedProblems) { this.trackedSolvedProblems = trackedSolvedProblems; }
    public int getPlatformsConnected() { return platformsConnected; }
    public void setPlatformsConnected(int platformsConnected) { this.platformsConnected = platformsConnected; }
    public int getTotalPlatformSolved() { return totalPlatformSolved; }
    public void setTotalPlatformSolved(int totalPlatformSolved) { this.totalPlatformSolved = totalPlatformSolved; }
    public int getTotalContests() { return totalContests; }
    public void setTotalContests(int totalContests) { this.totalContests = totalContests; }
    public int getGoalsTotal() { return goalsTotal; }
    public void setGoalsTotal(int goalsTotal) { this.goalsTotal = goalsTotal; }
    public int getGoalsActive() { return goalsActive; }
    public void setGoalsActive(int goalsActive) { this.goalsActive = goalsActive; }
    public int getGoalsCompleted() { return goalsCompleted; }
    public void setGoalsCompleted(int goalsCompleted) { this.goalsCompleted = goalsCompleted; }
    public int getPlannerTasksTotal() { return plannerTasksTotal; }
    public void setPlannerTasksTotal(int plannerTasksTotal) { this.plannerTasksTotal = plannerTasksTotal; }
    public int getPlannerTasksCompleted() { return plannerTasksCompleted; }
    public void setPlannerTasksCompleted(int plannerTasksCompleted) { this.plannerTasksCompleted = plannerTasksCompleted; }
    public int getAchievementsTotal() { return achievementsTotal; }
    public void setAchievementsTotal(int achievementsTotal) { this.achievementsTotal = achievementsTotal; }
    public int getAchievementsUnlocked() { return achievementsUnlocked; }
    public void setAchievementsUnlocked(int achievementsUnlocked) { this.achievementsUnlocked = achievementsUnlocked; }
    public List<PlatformStatistics> getPlatforms() { return platforms; }
    public void setPlatforms(List<PlatformStatistics> platforms) {
        this.platforms = platforms == null ? new ArrayList<>() : platforms;
    }
}
