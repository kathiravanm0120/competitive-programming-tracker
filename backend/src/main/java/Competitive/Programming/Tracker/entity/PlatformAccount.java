package Competitive.Programming.Tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "platform_accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_platform_account_user_platform",
                columnNames = {"user_id", "platform"}
        )
)
public class PlatformAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String platform;

    @Column(nullable = false, length = 100)
    private String handle;

    @Column(name = "profile_url", length = 500)
    private String profileUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PlatformAccount() {
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

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setUser(User user) {
        this.user = user;
    }
}
