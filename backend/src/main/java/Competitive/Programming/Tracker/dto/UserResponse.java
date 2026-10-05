package Competitive.Programming.Tracker.dto;

import Competitive.Programming.Tracker.entity.User;
import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String profileImage;
    private String bio;
    private LocalDateTime createdAt;
    private String role;
    private boolean enabled;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        this.bio = user.getBio();
        this.createdAt = user.getCreatedAt();
        this.role = user.getRole().name();
        this.enabled = user.isEnabled();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfileImage() { return profileImage; }
    public String getBio() { return bio; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
}
