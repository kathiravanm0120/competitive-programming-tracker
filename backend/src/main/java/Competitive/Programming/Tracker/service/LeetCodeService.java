package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.LeetCodeProfileResponse;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LeetCodeService {

    private static final String GRAPHQL_URL = "https://leetcode.com/graphql";

    private static final String QUERY = """
            query leetCodeProfile($username: String!) {
              matchedUser(username: $username) {
                username
                profile {
                  ranking
                  userAvatar
                  realName
                  aboutMe
                  countryName
                }
                submitStats {
                  acSubmissionNum {
                    difficulty
                    count
                    submissions
                  }
                  totalSubmissionNum {
                    difficulty
                    count
                    submissions
                  }
                }
              }
            }
            """;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LeetCodeService(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public LeetCodeProfileResponse getProfile(String username) {
        String normalizedUsername = normalizeUsername(username);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("operationName", "leetCodeProfile");
        requestBody.put("variables", Map.of("username", normalizedUsername));
        requestBody.put("query", QUERY);

        final String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(requestBody);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not build LeetCode GraphQL request", exception);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GRAPHQL_URL))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Origin", "https://leetcode.com")
                .header("Referer", "https://leetcode.com/")
                .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 Chrome/142.0.0.0 Safari/537.36"
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "LeetCode request failed with status " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());

            JsonNode errors = root.path("errors");
            if (errors.isArray() && !errors.isEmpty()) {
                String message = errors.get(0).path("message")
                        .asText("Unknown LeetCode GraphQL error");
                throw new IllegalArgumentException(message);
            }

            JsonNode matchedUser = root.path("data").path("matchedUser");
            if (matchedUser.isMissingNode() || matchedUser.isNull() || matchedUser.isEmpty()) {
                throw new IllegalArgumentException(
                        "LeetCode username not found: " + normalizedUsername);
            }

            return mapProfile(matchedUser, normalizedUsername);

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "LeetCode request was interrupted", exception);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not connect to LeetCode", exception);
        }
    }

    private LeetCodeProfileResponse mapProfile(
            JsonNode matchedUser,
            String fallbackUsername) {

        LeetCodeProfileResponse response = new LeetCodeProfileResponse();

        response.setUsername(textOrDefault(
                matchedUser, "username", fallbackUsername));

        JsonNode profile = matchedUser.path("profile");
        if (profile.isObject()) {
            if (profile.has("ranking") && !profile.get("ranking").isNull()) {
                response.setRanking(profile.get("ranking").asInt());
            }

            response.setAvatar(textOrNull(profile, "userAvatar"));
            response.setRealName(textOrNull(profile, "realName"));
            response.setAboutMe(textOrNull(profile, "aboutMe"));
            response.setCountryName(textOrNull(profile, "countryName"));
        }

        JsonNode submitStats = matchedUser.path("submitStats");

        int easySolved = getDifficultyCount(
                submitStats.path("acSubmissionNum"), "Easy");
        int mediumSolved = getDifficultyCount(
                submitStats.path("acSubmissionNum"), "Medium");
        int hardSolved = getDifficultyCount(
                submitStats.path("acSubmissionNum"), "Hard");

        int totalSubmissions = getDifficultySubmissions(
                submitStats.path("totalSubmissionNum"), "All");
        int acceptedSubmissions = getDifficultySubmissions(
                submitStats.path("acSubmissionNum"), "All");

        // Some accounts can omit the aggregate All row. Fall back to summing difficulties.
        if (totalSubmissions == 0) {
            totalSubmissions = sumSubmissionValues(
                    submitStats.path("totalSubmissionNum"));
        }

        if (acceptedSubmissions == 0) {
            acceptedSubmissions = sumSubmissionValues(
                    submitStats.path("acSubmissionNum"));
        }

        response.setEasySolved(easySolved);
        response.setMediumSolved(mediumSolved);
        response.setHardSolved(hardSolved);
        response.setTotalSolved(easySolved + mediumSolved + hardSolved);
        response.setTotalSubmissions(totalSubmissions);
        response.setAcceptedSubmissions(acceptedSubmissions);

        if (totalSubmissions > 0) {
            response.setAcceptanceRate(
                    Math.round(
                            (acceptedSubmissions * 10000.0 / totalSubmissions)
                    ) / 100.0
            );
        } else {
            response.setAcceptanceRate(0.0);
        }

        return response;
    }

    private int getDifficultyCount(JsonNode array, String difficulty) {
        if (!array.isArray()) {
            return 0;
        }

        for (JsonNode item : array) {
            if (difficulty.equalsIgnoreCase(
                    item.path("difficulty").asText())) {
                return item.path("count").asInt(0);
            }
        }

        return 0;
    }

    private int getDifficultySubmissions(JsonNode array, String difficulty) {
        if (!array.isArray()) {
            return 0;
        }

        for (JsonNode item : array) {
            if (difficulty.equalsIgnoreCase(
                    item.path("difficulty").asText())) {
                return item.path("submissions").asInt(0);
            }
        }

        return 0;
    }

    private int sumSubmissionValues(JsonNode array) {
        if (!array.isArray()) {
            return 0;
        }

        int total = 0;
        for (JsonNode item : array) {
            total += item.path("submissions").asInt(0);
        }
        return total;
    }

    private String normalizeUsername(String username) {
        String normalized = username == null ? "" : username.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    "LeetCode username is required");
        }

        return normalized;
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String textOrDefault(
            JsonNode node,
            String field,
            String fallback) {

        String value = textOrNull(node, field);
        return value == null || value.isBlank() ? fallback : value;
    }
}
