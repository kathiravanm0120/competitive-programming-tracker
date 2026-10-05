package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.CodeforcesRatingChangeResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionSummaryResponse;
import Competitive.Programming.Tracker.dto.CodeforcesUserResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionCodeResponse;
import Competitive.Programming.Tracker.entity.PlatformAccount;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.PlatformAccountRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CodeforcesService {

    private static final String API_BASE_URL = "https://codeforces.com/api/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final PlatformAccountRepository platformAccountRepository;
    private final UserRepository userRepository;

    public CodeforcesService(
            ObjectMapper objectMapper,
            PlatformAccountRepository platformAccountRepository,
            UserRepository userRepository) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.platformAccountRepository = platformAccountRepository;
        this.userRepository = userRepository;
    }

    public CodeforcesUserResponse getUser(String handle) {
        String normalizedHandle = normalizeHandle(handle);
        String url = API_BASE_URL + "user.info?handles=" + encode(normalizedHandle);

        JsonNode root = getJson(url);
        JsonNode result = root.path("result");

        if (!result.isArray() || result.isEmpty()) {
            throw new IllegalArgumentException(
                    "Codeforces user not found: " + normalizedHandle);
        }

        return mapUser(result.get(0));
    }

    public CodeforcesSubmissionSummaryResponse getSubmissionSummary(String handle) {
        String normalizedHandle = normalizeHandle(handle);
        String url = API_BASE_URL + "user.status?handle=" + encode(normalizedHandle) + "&from=1&count=1000";

        JsonNode root = getJson(url);
        JsonNode result = root.path("result");

        if (!result.isArray()) {
            throw new IllegalArgumentException(
                    "No submission history found for: " + normalizedHandle);
        }

        List<CodeforcesSubmissionResponse> submissions = new ArrayList<>();
        Map<String, Integer> verdictCounts = new LinkedHashMap<>();
        Map<String, Integer> languageCounts = new LinkedHashMap<>();
        Set<String> solvedProblems = new HashSet<>();
        int acceptedCount = 0;

        for (JsonNode item : result) {
            JsonNode problem = item.path("problem");
            String verdict = textOrDefault(item, "verdict", "UNKNOWN");
            String language = textOrDefault(item, "programmingLanguage", "Unknown");

            verdictCounts.merge(verdict, 1, Integer::sum);
            languageCounts.merge(language, 1, Integer::sum);

            if ("OK".equalsIgnoreCase(verdict)) {
                acceptedCount++;
                solvedProblems.add(
                        item.path("contestId").asText("-") + ":" +
                        problem.path("index").asText("-")
                );
            }

            CodeforcesSubmissionResponse dto = new CodeforcesSubmissionResponse();
            dto.setId(item.path("id").asLong());
            dto.setContestId(item.path("contestId").asInt());
            dto.setProblemIndex(problem.has("index") ? problem.path("index").asText() : null);
            dto.setProblemName(textOrDefault(problem, "name", "Unknown problem"));
            dto.setProblemRating(problem.has("rating") ? String.valueOf(problem.path("rating").asInt()) : null);
            dto.setCreationTimeSeconds(item.path("creationTimeSeconds").asLong());
            dto.setRelativeTimeSeconds(item.has("relativeTimeSeconds") ? item.path("relativeTimeSeconds").asInt() : null);
            dto.setVerdict(verdict);
            dto.setProgrammingLanguage(language);
            dto.setTestset(textOrDefault(item, "testset", null));
            dto.setPassedTestCount(item.has("passedTestCount") ? item.path("passedTestCount").asInt() : null);
            dto.setTimeConsumedMillis(item.has("timeConsumedMillis") ? item.path("timeConsumedMillis").asLong() : null);
            dto.setMemoryConsumedBytes(item.has("memoryConsumedBytes") ? item.path("memoryConsumedBytes").asLong() : null);
            submissions.add(dto);
        }

        CodeforcesSubmissionSummaryResponse response = new CodeforcesSubmissionSummaryResponse();
        response.setHandle(normalizedHandle);
        response.setSyncedSubmissionCount(submissions.size());
        response.setAcceptedSubmissionCount(acceptedCount);
        response.setSolvedProblemCount(solvedProblems.size());
        response.setVerdictCounts(verdictCounts);
        response.setLanguageCounts(languageCounts);
        response.setRecentSubmissions(
                submissions.stream()
                        .sorted(Comparator.comparing(CodeforcesSubmissionResponse::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(25)
                        .collect(Collectors.toList())
        );
        return response;
    }

    public CodeforcesSubmissionCodeResponse getSubmissionCode(
            String username,
            String handle,
            Long contestId,
            Long submissionId) {

        if (submissionId == null || submissionId <= 0) {
            throw new IllegalArgumentException("Invalid Codeforces submission id");
        }

        if (contestId == null || contestId <= 0) {
            throw new IllegalArgumentException("Invalid Codeforces contest id");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PlatformAccount account = platformAccountRepository
                .findByUserAndPlatform(user, "Codeforces")
                .orElseThrow(() -> new IllegalArgumentException(
                        "Connect a Codeforces account before viewing submitted code."
                ));

        if (!account.getHandle().equalsIgnoreCase(handle == null ? "" : handle.trim())) {
            throw new IllegalArgumentException(
                    "You can only view submissions for your connected Codeforces account."
            );
        }

        String submissionUrl =
                "https://codeforces.com/contest/%d/submission/%d"
                        .formatted(contestId, submissionId);

        // Codeforces documents includeSources as an own-account-only API option.
        // This project does not store Codeforces API credentials, so the viewer reads
        // the public submission page and provides the original Codeforces link as fallback.
        Document document;
        try {
            document = Jsoup.connect(submissionUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 Chrome/154 Safari/537.36")
                    .referrer("https://codeforces.com/")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Could not fetch the Codeforces submission page. " +
                    "Use the 'Open on Codeforces' link instead.", ex);
        }

        var sourceNode = document.selectFirst("pre#program-source-text");
        if (sourceNode == null) {
            sourceNode = document.selectFirst("pre.program-source");
        }

        if (sourceNode == null || sourceNode.text().isBlank()) {
            throw new IllegalStateException(
                    "Codeforces did not expose source code for this submission. " +
                    "Use the 'Open on Codeforces' link instead.");
        }

        CodeforcesSubmissionCodeResponse response =
                new CodeforcesSubmissionCodeResponse();
        response.setSubmissionId(submissionId);
        response.setSourceCode(sourceNode.text());
        response.setSubmissionUrl(submissionUrl);
        return response;
    }

    public List<CodeforcesRatingChangeResponse> getRatingHistory(String handle) {
        String normalizedHandle = normalizeHandle(handle);
        String url = API_BASE_URL + "user.rating?handle=" + encode(normalizedHandle);

        JsonNode root = getJson(url);
        JsonNode result = root.path("result");

        if (!result.isArray()) {
            throw new IllegalArgumentException(
                    "No Codeforces rating history found for: " + normalizedHandle);
        }

        List<CodeforcesRatingChangeResponse> history = new ArrayList<>();

        for (JsonNode item : result) {
            CodeforcesRatingChangeResponse change = new CodeforcesRatingChangeResponse();

            change.setContestId(item.path("contestId").asInt());
            change.setContestName(item.path("contestName").asText());
            change.setHandle(item.path("handle").asText());
            change.setRank(item.path("rank").asInt());
            change.setRatingUpdateTimeSeconds(
                    item.path("ratingUpdateTimeSeconds").asLong());
            change.setOldRating(item.path("oldRating").asInt());
            change.setNewRating(item.path("newRating").asInt());

            history.add(change);
        }

        return history;
    }

    private JsonNode getJson(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("User-Agent", "CompetitiveProgrammingTracker/1.0")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalArgumentException(
                        "Codeforces API request failed with status " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String status = root.path("status").asText();

            if (!"OK".equalsIgnoreCase(status)) {
                String comment = root.path("comment")
                        .asText("Unknown Codeforces API error");
                throw new IllegalArgumentException(comment);
            }

            return root;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Codeforces API request was interrupted", exception);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not connect to Codeforces API", exception);
        }
    }

    private CodeforcesUserResponse mapUser(JsonNode user) {
        CodeforcesUserResponse response = new CodeforcesUserResponse();

        response.setHandle(textOrNull(user, "handle"));
        response.setFirstName(textOrNull(user, "firstName"));
        response.setLastName(textOrNull(user, "lastName"));
        response.setCountry(textOrNull(user, "country"));
        response.setCity(textOrNull(user, "city"));
        response.setOrganization(textOrNull(user, "organization"));
        response.setRank(textOrNull(user, "rank"));
        response.setMaxRank(textOrNull(user, "maxRank"));
        response.setAvatar(textOrNull(user, "avatar"));
        response.setTitlePhoto(textOrNull(user, "titlePhoto"));

        if (user.has("contribution")) {
            response.setContribution(user.get("contribution").asInt());
        }
        if (user.has("rating")) {
            response.setRating(user.get("rating").asInt());
        }
        if (user.has("maxRating")) {
            response.setMaxRating(user.get("maxRating").asInt());
        }
        if (user.has("lastOnlineTimeSeconds")) {
            response.setLastOnlineTimeSeconds(
                    user.get("lastOnlineTimeSeconds").asLong());
        }
        if (user.has("registrationTimeSeconds")) {
            response.setRegistrationTimeSeconds(
                    user.get("registrationTimeSeconds").asLong());
        }
        if (user.has("friendOfCount")) {
            response.setFriendOfCount(user.get("friendOfCount").asInt());
        }

        return response;
    }

    private String normalizeHandle(String handle) {
        String normalizedHandle = handle == null ? "" : handle.trim();

        if (normalizedHandle.isBlank()) {
            throw new IllegalArgumentException("Codeforces handle is required");
        }

        return normalizedHandle;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String textOrDefault(JsonNode node, String field, String fallback) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? fallback : value.asText();
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
