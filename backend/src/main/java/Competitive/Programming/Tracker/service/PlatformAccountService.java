package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.PlatformAccountRequest;
import Competitive.Programming.Tracker.dto.PlatformAccountResponse;
import Competitive.Programming.Tracker.entity.PlatformAccount;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.PlatformAccountRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PlatformAccountService {

    private static final Set<String> SUPPORTED_PLATFORMS = Set.of(
            "Codeforces",
            "LeetCode",
            "CodeChef",
            "AtCoder"
    );

    private final PlatformAccountRepository platformAccountRepository;
    private final UserRepository userRepository;

    public PlatformAccountService(
            PlatformAccountRepository platformAccountRepository,
            UserRepository userRepository) {
        this.platformAccountRepository = platformAccountRepository;
        this.userRepository = userRepository;
    }

    public List<PlatformAccountResponse> getAccounts(String username) {
        User user = getUser(username);

        return platformAccountRepository.findByUserOrderByIdAsc(user)
                .stream()
                .map(PlatformAccountResponse::new)
                .toList();
    }

    public PlatformAccountResponse saveAccount(
            String username,
            PlatformAccountRequest request) {

        User user = getUser(username);
        String platform = normalizePlatform(request.getPlatform());
        String handle = normalize(request.getHandle());

        validatePlatform(platform);

        if (!StringUtils.hasText(handle)) {
            throw new IllegalArgumentException("Platform handle is required");
        }

        PlatformAccount account = platformAccountRepository
                .findByUserAndPlatform(user, platform)
                .orElseGet(PlatformAccount::new);

        account.setUser(user);
        account.setPlatform(platform);
        account.setHandle(handle);
        account.setProfileUrl(normalize(request.getProfileUrl()));

        return new PlatformAccountResponse(
                platformAccountRepository.save(account));
    }

    public void deleteAccount(String username, String platform) {
        User user = getUser(username);
        String normalizedPlatform = normalizePlatform(platform);
        validatePlatform(normalizedPlatform);

        platformAccountRepository
                .findByUserAndPlatform(user, normalizedPlatform)
                .ifPresent(platformAccountRepository::delete);
    }

    private String normalizePlatform(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }

        String input = value.trim();
        return SUPPORTED_PLATFORMS.stream()
                .filter(platform -> platform.equalsIgnoreCase(input))
                .findFirst()
                .orElse(input);
    }

    private void validatePlatform(String platform) {
        if (!SUPPORTED_PLATFORMS.contains(platform)) {
            throw new IllegalArgumentException(
                    "Unsupported platform. Use Codeforces, LeetCode, CodeChef, or AtCoder.");
        }
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
