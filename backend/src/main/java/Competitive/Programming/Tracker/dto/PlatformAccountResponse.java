package Competitive.Programming.Tracker.dto;

import Competitive.Programming.Tracker.entity.PlatformAccount;

public class PlatformAccountResponse {

    private Long id;
    private String platform;
    private String handle;
    private String profileUrl;

    public PlatformAccountResponse(PlatformAccount account) {
        this.id = account.getId();
        this.platform = account.getPlatform();
        this.handle = account.getHandle();
        this.profileUrl = account.getProfileUrl();
    }

    public Long getId() {
        return id;
    }

    public String getPlatform() {
        return platform;
    }

    public String getHandle() {
        return handle;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
