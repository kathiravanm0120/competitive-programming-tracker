package Competitive.Programming.Tracker.dto;

public class AtCoderProfileResponse {

    private String username;
    private String rank;
    private Integer rating;
    private Integer highestRating;
    private Integer ratedMatches;
    private String lastCompeted;
    private String country;
    private String affiliation;
    private Integer winCount;
    private String profileUrl;

    public AtCoderProfileResponse() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getHighestRating() {
        return highestRating;
    }

    public void setHighestRating(Integer highestRating) {
        this.highestRating = highestRating;
    }

    public Integer getRatedMatches() {
        return ratedMatches;
    }

    public void setRatedMatches(Integer ratedMatches) {
        this.ratedMatches = ratedMatches;
    }

    public String getLastCompeted() {
        return lastCompeted;
    }

    public void setLastCompeted(String lastCompeted) {
        this.lastCompeted = lastCompeted;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public Integer getWinCount() {
        return winCount;
    }

    public void setWinCount(Integer winCount) {
        this.winCount = winCount;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
