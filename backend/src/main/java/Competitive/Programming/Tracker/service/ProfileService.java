package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.ProfileUpdateRequest;
import Competitive.Programming.Tracker.dto.UserResponse;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getProfile(String username) {
        return new UserResponse(getUser(username));
    }

    public UserResponse updateProfile(
            String username,
            ProfileUpdateRequest request) {

        User user = getUser(username);

        String email = request.getEmail() == null
                ? ""
                : request.getEmail().trim();

        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!email.equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setEmail(email);
        user.setBio(normalize(request.getBio()));
        user.setProfileImage(normalize(request.getProfileImage()));

        return new UserResponse(userRepository.save(user));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
