package Competitive.Programming.Tracker.dto;

public class CodeChefProfileResponse {

    private String username;
    private Integer currentRating;
    private Integer highestRating;
    private Integer stars;
    private String globalRank;
    private String countryRank;
    private Integer contestsParticipated;
    private Integer problemsSolved;
    private String profileUrl;
    private String country;

    public CodeChefProfileResponse() {
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getCurrentRating() { return currentRating; }
    public void setCurrentRating(Integer currentRating) { this.currentRating = currentRating; }

    public Integer getHighestRating() { return highestRating; }
    public void setHighestRating(Integer highestRating) { this.highestRating = highestRating; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public String getGlobalRank() { return globalRank; }
    public void setGlobalRank(String globalRank) { this.globalRank = globalRank; }

    public String getCountryRank() { return countryRank; }
    public void setCountryRank(String countryRank) { this.countryRank = countryRank; }

    public Integer getContestsParticipated() { return contestsParticipated; }
    public void setContestsParticipated(Integer contestsParticipated) { this.contestsParticipated = contestsParticipated; }

    public Integer getProblemsSolved() { return problemsSolved; }
    public void setProblemsSolved(Integer problemsSolved) { this.problemsSolved = problemsSolved; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
