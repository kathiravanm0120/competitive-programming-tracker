package Competitive.Programming.Tracker.dto;

public class CodeforcesSubmissionResponse {
    private Long id;
    private Integer contestId;
    private String problemIndex;
    private String problemName;
    private String problemRating;
    private Long creationTimeSeconds;
    private Integer relativeTimeSeconds;
    private String verdict;
    private String programmingLanguage;
    private String testset;
    private Integer passedTestCount;
    private Long timeConsumedMillis;
    private Long memoryConsumedBytes;

    public CodeforcesSubmissionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getContestId() { return contestId; }
    public void setContestId(Integer contestId) { this.contestId = contestId; }
    public String getProblemIndex() { return problemIndex; }
    public void setProblemIndex(String problemIndex) { this.problemIndex = problemIndex; }
    public String getProblemName() { return problemName; }
    public void setProblemName(String problemName) { this.problemName = problemName; }
    public String getProblemRating() { return problemRating; }
    public void setProblemRating(String problemRating) { this.problemRating = problemRating; }
    public Long getCreationTimeSeconds() { return creationTimeSeconds; }
    public void setCreationTimeSeconds(Long creationTimeSeconds) { this.creationTimeSeconds = creationTimeSeconds; }
    public Integer getRelativeTimeSeconds() { return relativeTimeSeconds; }
    public void setRelativeTimeSeconds(Integer relativeTimeSeconds) { this.relativeTimeSeconds = relativeTimeSeconds; }
    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }
    public String getProgrammingLanguage() { return programmingLanguage; }
    public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }
    public String getTestset() { return testset; }
    public void setTestset(String testset) { this.testset = testset; }
    public Integer getPassedTestCount() { return passedTestCount; }
    public void setPassedTestCount(Integer passedTestCount) { this.passedTestCount = passedTestCount; }
    public Long getTimeConsumedMillis() { return timeConsumedMillis; }
    public void setTimeConsumedMillis(Long timeConsumedMillis) { this.timeConsumedMillis = timeConsumedMillis; }
    public Long getMemoryConsumedBytes() { return memoryConsumedBytes; }
    public void setMemoryConsumedBytes(Long memoryConsumedBytes) { this.memoryConsumedBytes = memoryConsumedBytes; }
}
