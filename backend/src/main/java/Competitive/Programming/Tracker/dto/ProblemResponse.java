package Competitive.Programming.Tracker.dto;

import Competitive.Programming.Tracker.entity.Problem;

public class ProblemResponse {

    private Long id;
    private String title;
    private String platform;
    private String url;
    private String difficulty;
    private String topic;
    private String status;
    private String notes;
    private boolean favorite;

    public ProblemResponse(Problem problem) {
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.platform = problem.getPlatform();
        this.url = problem.getUrl();
        this.difficulty = problem.getDifficulty();
        this.topic = problem.getTopic();
        this.status = problem.getStatus();
        this.notes = problem.getNotes();
        this.favorite = problem.isFavorite();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPlatform() {
        return platform;
    }

    public String getUrl() {
        return url;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isFavorite() {
        return favorite;
    }
}