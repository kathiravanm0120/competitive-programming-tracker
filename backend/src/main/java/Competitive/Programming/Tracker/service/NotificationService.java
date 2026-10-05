package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.AchievementResponse;
import Competitive.Programming.Tracker.dto.NotificationResponse;
import Competitive.Programming.Tracker.entity.Notification;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.NotificationRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            AchievementService achievementService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    @Transactional
    public List<NotificationResponse> getNotifications(String username) {
        User user = getUser(username);
        refreshAchievementNotifications(user);

        return notificationRepository
                .findTop50ByUserOrderByCreatedAtDescIdDesc(user)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = getUser(username);
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public NotificationResponse markAsRead(Long id, String username) {
        User user = getUser(username);
        Notification notification = notificationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return new NotificationResponse(notification);
    }

    @Transactional
    public void markAllAsRead(String username) {
        User user = getUser(username);
        List<Notification> notifications =
                notificationRepository.findByUserAndReadFalse(user);

        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                notification.setReadAt(now);
            }
        }

        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteNotification(Long id, String username) {
        User user = getUser(username);
        Notification notification = notificationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
    }

    @Transactional
    public void createAchievementNotification(
            User user,
            AchievementResponse achievement) {

        String referenceKey = "ACHIEVEMENT:" + achievement.getCode();

        if (notificationRepository.existsByUserAndReferenceKey(
                user,
                referenceKey)) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Achievement Unlocked");
        notification.setMessage(
                achievement.getTitle() + " — " + achievement.getDescription()
        );
        notification.setType("ACHIEVEMENT");
        notification.setLink("/achievements");
        notification.setReferenceKey(referenceKey);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Transactional
    public void createGoalNotification(
            User user,
            Long goalId,
            String title,
            String message,
            String referenceSuffix) {

        String referenceKey = "GOAL:" + goalId + ":" + referenceSuffix;

        if (notificationRepository.existsByUserAndReferenceKey(
                user,
                referenceKey)) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("GOAL");
        notification.setLink("/goals");
        notification.setReferenceKey(referenceKey);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Transactional
    public void createPlannerNotification(
            User user,
            Long taskId,
            String title,
            String message) {

        String referenceKey = "PLANNER_TASK:" + taskId + ":COMPLETED";

        if (notificationRepository.existsByUserAndReferenceKey(
                user,
                referenceKey)) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("PLANNER");
        notification.setLink("/planner");
        notification.setReferenceKey(referenceKey);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    private void refreshAchievementNotifications(User user) {
        List<AchievementResponse> achievements =
                achievementService.getAchievements(user.getUsername());

        achievements.stream()
                .filter(AchievementResponse::isUnlocked)
                .forEach(achievement ->
                        createAchievementNotification(user, achievement));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
