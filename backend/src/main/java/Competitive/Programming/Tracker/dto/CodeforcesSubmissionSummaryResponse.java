package Competitive.Programming.Tracker.dto;

import java.util.List;
import java.util.Map;

public class CodeforcesSubmissionSummaryResponse {
    private String handle;
    private Integer syncedSubmissionCount;
    private Integer acceptedSubmissionCount;
    private Integer solvedProblemCount;
    private Map<String, Integer> verdictCounts;
    private Map<String, Integer> languageCounts;
    private List<CodeforcesSubmissionResponse> recentSubmissions;

    public CodeforcesSubmissionSummaryResponse() {}

    public String getHandle() { return handle; }
    public void setHandle(String handle) { this.handle = handle; }
    public Integer getSyncedSubmissionCount() { return syncedSubmissionCount; }
    public void setSyncedSubmissionCount(Integer syncedSubmissionCount) { this.syncedSubmissionCount = syncedSubmissionCount; }
    public Integer getAcceptedSubmissionCount() { return acceptedSubmissionCount; }
    public void setAcceptedSubmissionCount(Integer acceptedSubmissionCount) { this.acceptedSubmissionCount = acceptedSubmissionCount; }
    public Integer getSolvedProblemCount() { return solvedProblemCount; }
    public void setSolvedProblemCount(Integer solvedProblemCount) { this.solvedProblemCount = solvedProblemCount; }
    public Map<String, Integer> getVerdictCounts() { return verdictCounts; }
    public void setVerdictCounts(Map<String, Integer> verdictCounts) { this.verdictCounts = verdictCounts; }
    public Map<String, Integer> getLanguageCounts() { return languageCounts; }
    public void setLanguageCounts(Map<String, Integer> languageCounts) { this.languageCounts = languageCounts; }
    public List<CodeforcesSubmissionResponse> getRecentSubmissions() { return recentSubmissions; }
    public void setRecentSubmissions(List<CodeforcesSubmissionResponse> recentSubmissions) { this.recentSubmissions = recentSubmissions; }
}
