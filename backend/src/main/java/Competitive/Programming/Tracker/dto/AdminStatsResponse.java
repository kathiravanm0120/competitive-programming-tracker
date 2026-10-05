package Competitive.Programming.Tracker.dto;

public class AdminStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long disabledUsers;
    private long adminUsers;
    private long totalProblems;
    private long totalPlatformAccounts;
    private long totalGoals;
    private long totalPlannerTasks;
    private long completedPlannerTasks;
    private long totalNotifications;

    public AdminStatsResponse(
            long totalUsers,
            long activeUsers,
            long disabledUsers,
            long adminUsers,
            long totalProblems,
            long totalPlatformAccounts,
            long totalGoals,
            long totalPlannerTasks,
            long completedPlannerTasks,
            long totalNotifications) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.disabledUsers = disabledUsers;
        this.adminUsers = adminUsers;
        this.totalProblems = totalProblems;
        this.totalPlatformAccounts = totalPlatformAccounts;
        this.totalGoals = totalGoals;
        this.totalPlannerTasks = totalPlannerTasks;
        this.completedPlannerTasks = completedPlannerTasks;
        this.totalNotifications = totalNotifications;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getActiveUsers() { return activeUsers; }
    public long getDisabledUsers() { return disabledUsers; }
    public long getAdminUsers() { return adminUsers; }
    public long getTotalProblems() { return totalProblems; }
    public long getTotalPlatformAccounts() { return totalPlatformAccounts; }
    public long getTotalGoals() { return totalGoals; }
    public long getTotalPlannerTasks() { return totalPlannerTasks; }
    public long getCompletedPlannerTasks() { return completedPlannerTasks; }
    public long getTotalNotifications() { return totalNotifications; }
}
