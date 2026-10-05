package Competitive.Programming.Tracker.dto;

public class PlatformAccountRequest {

    private String platform;
    private String handle;
    private String profileUrl;

    public PlatformAccountRequest() {
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

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
