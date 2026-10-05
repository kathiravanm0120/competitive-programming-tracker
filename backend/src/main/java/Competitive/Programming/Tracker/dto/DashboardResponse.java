package Competitive.Programming.Tracker.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardResponse {

    private String username;
    private TrackerStats tracker;
    private OverallStats overall;
    private List<PlatformStatistics> platforms = new ArrayList<>();
    private CodeforcesDashboard codeforces;

    public DashboardResponse() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TrackerStats getTracker() {
        return tracker;
    }

    public void setTracker(TrackerStats tracker) {
        this.tracker = tracker;
    }

    public OverallStats getOverall() {
        return overall;
    }

    public void setOverall(OverallStats overall) {
        this.overall = overall;
    }

    public List<PlatformStatistics> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<PlatformStatistics> platforms) {
        this.platforms = platforms == null ? new ArrayList<>() : platforms;
    }

    public CodeforcesDashboard getCodeforces() {
        return codeforces;
    }

    public void setCodeforces(CodeforcesDashboard codeforces) {
        this.codeforces = codeforces;
    }

    public static class TrackerStats {
        private int totalProblems;
        private int solvedProblems;
        private int attemptedProblems;
        private int unsolvedProblems;
        private int favoriteProblems;
        private int platformCount;

        public int getTotalProblems() { return totalProblems; }
        public void setTotalProblems(int value) { this.totalProblems = value; }
        public int getSolvedProblems() { return solvedProblems; }
        public void setSolvedProblems(int value) { this.solvedProblems = value; }
        public int getAttemptedProblems() { return attemptedProblems; }
        public void setAttemptedProblems(int value) { this.attemptedProblems = value; }
        public int getUnsolvedProblems() { return unsolvedProblems; }
        public void setUnsolvedProblems(int value) { this.unsolvedProblems = value; }
        public int getFavoriteProblems() { return favoriteProblems; }
        public void setFavoriteProblems(int value) { this.favoriteProblems = value; }
        public int getPlatformCount() { return platformCount; }
        public void setPlatformCount(int value) { this.platformCount = value; }
    }

    public static class OverallStats {
        private int platformsConnected;
        private int platformsAvailable;
        private int ratedPlatforms;
        private int totalSolved;
        private int totalContests;
        private int trackerSolved;
        private int trackedProblems;
        private String uniqueProblemCountNote;

        public int getPlatformsConnected() { return platformsConnected; }
        public void setPlatformsConnected(int value) { this.platformsConnected = value; }
        public int getPlatformsAvailable() { return platformsAvailable; }
        public void setPlatformsAvailable(int value) { this.platformsAvailable = value; }
        public int getRatedPlatforms() { return ratedPlatforms; }
        public void setRatedPlatforms(int value) { this.ratedPlatforms = value; }
        public int getTotalSolved() { return totalSolved; }
        public void setTotalSolved(int value) { this.totalSolved = value; }
        public int getTotalContests() { return totalContests; }
        public void setTotalContests(int value) { this.totalContests = value; }
        public int getTrackerSolved() { return trackerSolved; }
        public void setTrackerSolved(int value) { this.trackerSolved = value; }
        public int getTrackedProblems() { return trackedProblems; }
        public void setTrackedProblems(int value) { this.trackedProblems = value; }
        public String getUniqueProblemCountNote() { return uniqueProblemCountNote; }
        public void setUniqueProblemCountNote(String value) { this.uniqueProblemCountNote = value; }
    }

    public static class CodeforcesDashboard {
        private boolean connected;
        private String handle;
        private Integer rating;
        private Integer maxRating;
        private String rank;
        private String maxRank;
        private Integer contribution;
        private String error;
        private int contestCount;
        private int solvedProblemCount;
        private int acceptedSubmissionCount;
        private int syncedSubmissionCount;
        private Map<String, Integer> verdictCounts = new LinkedHashMap<>();
        private Map<String, Integer> languageCounts = new LinkedHashMap<>();
        private List<CodeforcesRatingChangeResponse> ratingHistory = new ArrayList<>();
        private List<CodeforcesSubmissionResponse> recentSubmissions = new ArrayList<>();

        public boolean isConnected() { return connected; }
        public void setConnected(boolean value) { this.connected = value; }
        public String getHandle() { return handle; }
        public void setHandle(String value) { this.handle = value; }
        public Integer getRating() { return rating; }
        public void setRating(Integer value) { this.rating = value; }
        public Integer getMaxRating() { return maxRating; }
        public void setMaxRating(Integer value) { this.maxRating = value; }
        public String getRank() { return rank; }
        public void setRank(String value) { this.rank = value; }
        public String getMaxRank() { return maxRank; }
        public void setMaxRank(String value) { this.maxRank = value; }
        public Integer getContribution() { return contribution; }
        public void setContribution(Integer value) { this.contribution = value; }
        public String getError() { return error; }
        public void setError(String value) { this.error = value; }
        public int getContestCount() { return contestCount; }
        public void setContestCount(int value) { this.contestCount = value; }
        public int getSolvedProblemCount() { return solvedProblemCount; }
        public void setSolvedProblemCount(int value) { this.solvedProblemCount = value; }
        public int getAcceptedSubmissionCount() { return acceptedSubmissionCount; }
        public void setAcceptedSubmissionCount(int value) { this.acceptedSubmissionCount = value; }
        public int getSyncedSubmissionCount() { return syncedSubmissionCount; }
        public void setSyncedSubmissionCount(int value) { this.syncedSubmissionCount = value; }
        public Map<String, Integer> getVerdictCounts() { return verdictCounts; }
        public void setVerdictCounts(Map<String, Integer> value) { this.verdictCounts = value == null ? new LinkedHashMap<>() : value; }
        public Map<String, Integer> getLanguageCounts() { return languageCounts; }
        public void setLanguageCounts(Map<String, Integer> value) { this.languageCounts = value == null ? new LinkedHashMap<>() : value; }
        public List<CodeforcesRatingChangeResponse> getRatingHistory() { return ratingHistory; }
        public void setRatingHistory(List<CodeforcesRatingChangeResponse> value) { this.ratingHistory = value == null ? new ArrayList<>() : value; }
        public List<CodeforcesSubmissionResponse> getRecentSubmissions() { return recentSubmissions; }
        public void setRecentSubmissions(List<CodeforcesSubmissionResponse> value) { this.recentSubmissions = value == null ? new ArrayList<>() : value; }
    }
}
