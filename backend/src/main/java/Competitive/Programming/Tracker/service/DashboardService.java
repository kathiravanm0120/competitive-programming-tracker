package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.AtCoderProfileResponse;
import Competitive.Programming.Tracker.dto.CodeChefProfileResponse;
import Competitive.Programming.Tracker.dto.CodeforcesRatingChangeResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionSummaryResponse;
import Competitive.Programming.Tracker.dto.CodeforcesUserResponse;
import Competitive.Programming.Tracker.dto.DashboardResponse;
import Competitive.Programming.Tracker.dto.LeetCodeProfileResponse;
import Competitive.Programming.Tracker.dto.PlatformStatistics;
import Competitive.Programming.Tracker.entity.PlatformAccount;
import Competitive.Programming.Tracker.entity.Problem;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.PlatformAccountRepository;
import Competitive.Programming.Tracker.repository.ProblemRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DashboardService {

    private static final long CACHE_TTL_MILLIS = 60_000L;
    private static final long CODEFORCES_REQUEST_GAP_MILLIS = 2_150L;

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final PlatformAccountRepository platformAccountRepository;
    private final CodeforcesService codeforcesService;
    private final LeetCodeService leetCodeService;
    private final CodeChefService codeChefService;
    private final AtCoderService atCoderService;

    private final Map<String, CachedDashboard> cache = new ConcurrentHashMap<>();

    public DashboardService(
            UserRepository userRepository,
            ProblemRepository problemRepository,
            PlatformAccountRepository platformAccountRepository,
            CodeforcesService codeforcesService,
            LeetCodeService leetCodeService,
            CodeChefService codeChefService,
            AtCoderService atCoderService) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.platformAccountRepository = platformAccountRepository;
        this.codeforcesService = codeforcesService;
        this.leetCodeService = leetCodeService;
        this.codeChefService = codeChefService;
        this.atCoderService = atCoderService;
    }

    public DashboardResponse getDashboard(String username) {
        User user = getUser(username);
        String cacheKey = username.toLowerCase();

        CachedDashboard cached = cache.get(cacheKey);
        if (cached != null && isFresh(cached.createdAt)) {
            return cached.dashboard;
        }

        synchronized (cache.computeIfAbsent(cacheKey, key -> new CachedDashboard())) {
            cached = cache.get(cacheKey);

            if (cached.dashboard != null && isFresh(cached.createdAt)) {
                return cached.dashboard;
            }

            DashboardResponse response = buildDashboard(user, username);
            cached.dashboard = response;
            cached.createdAt = System.currentTimeMillis();
            return response;
        }
    }

    private DashboardResponse buildDashboard(User user, String username) {
        DashboardResponse response = new DashboardResponse();
        response.setUsername(username);
        response.setTracker(buildTrackerStats(user));

        List<PlatformStatistics> platforms = new ArrayList<>();
        List<PlatformAccount> accounts =
                platformAccountRepository.findByUserOrderByIdAsc(user);

        Map<String, PlatformAccount> accountByPlatform = new LinkedHashMap<>();
        for (PlatformAccount account : accounts) {
            if (account.getPlatform() != null) {
                accountByPlatform.put(
                        account.getPlatform().trim().toLowerCase(),
                        account
                );
            }
        }

        PlatformAccount codeforcesAccount = accountByPlatform.get("codeforces");
        DashboardResponse.CodeforcesDashboard codeforcesDashboard = null;

        if (codeforcesAccount != null && isValidHandle(codeforcesAccount.getHandle())) {
            try {
                codeforcesDashboard = buildCodeforcesDashboard(codeforcesAccount.getHandle().trim());
            } catch (RuntimeException exception) {
                codeforcesDashboard = new DashboardResponse.CodeforcesDashboard();
                codeforcesDashboard.setConnected(true);
                codeforcesDashboard.setHandle(codeforcesAccount.getHandle());
                codeforcesDashboard.setError(safeMessage(exception));
            }
        }

        platforms.add(buildCodeforcesStatistics(codeforcesAccount, codeforcesDashboard));
        platforms.add(buildLeetCodeStatistics(accountByPlatform.get("leetcode")));
        platforms.add(buildCodeChefStatistics(accountByPlatform.get("codechef")));
        platforms.add(buildAtCoderStatistics(accountByPlatform.get("atcoder")));

        response.setPlatforms(platforms);
        response.setOverall(buildOverallStats(platforms, response.getTracker()));

        // Keep the detailed Codeforces block for the richer dashboard widgets.
        response.setCodeforces(codeforcesDashboard);
        if (response.getCodeforces() == null) {
            DashboardResponse.CodeforcesDashboard empty =
                    new DashboardResponse.CodeforcesDashboard();
            empty.setConnected(false);
            response.setCodeforces(empty);
        }

        return response;
    }

    private DashboardResponse.OverallStats buildOverallStats(
            List<PlatformStatistics> platforms,
            DashboardResponse.TrackerStats tracker) {

        DashboardResponse.OverallStats stats = new DashboardResponse.OverallStats();

        int connected = 0;
        int available = 0;
        int totalSolved = 0;
        int totalContests = 0;
        int ratedPlatforms = 0;

        for (PlatformStatistics platform : platforms) {
            if (platform.isConnected()) {
                connected++;
            }

            if (platform.isAvailable()) {
                available++;
            }

            if (platform.getSolved() != null) {
                totalSolved += platform.getSolved();
            }

            if (platform.getContests() != null) {
                totalContests += platform.getContests();
            }

            if (platform.getRating() != null) {
                ratedPlatforms++;
            }
        }

        stats.setPlatformsConnected(connected);
        stats.setPlatformsAvailable(available);
        stats.setTotalSolved(totalSolved);
        stats.setTotalContests(totalContests);
        stats.setRatedPlatforms(ratedPlatforms);
        stats.setTrackerSolved(tracker.getSolvedProblems());
        stats.setTrackedProblems(tracker.getTotalProblems());
        stats.setUniqueProblemCountNote(
                "Platform solved totals are summed across platforms; the tracker count is separate and is not deduplicated across platforms."
        );

        return stats;
    }

    private PlatformStatistics buildCodeforcesStatistics(
            PlatformAccount account,
            DashboardResponse.CodeforcesDashboard dashboard) {

        PlatformStatistics stats = baseStatistics("Codeforces", account);

        if (!hasAccount(account) || dashboard == null) {
            return stats;
        }

        if (dashboard.getError() != null && !dashboard.getError().isBlank()) {
            markUnavailable(stats, new IllegalStateException(dashboard.getError()));
            return stats;
        }

        stats.setAvailable(true);
        stats.setHandle(dashboard.getHandle());
        stats.setProfileUrl(
                dashboard.getHandle() == null
                        ? account.getProfileUrl()
                        : "https://codeforces.com/profile/" + dashboard.getHandle());
        stats.setRating(dashboard.getRating());
        stats.setMaxRating(dashboard.getMaxRating());
        stats.setRank(dashboard.getRank());
        stats.setSolved(dashboard.getSolvedProblemCount());
        stats.setContests(dashboard.getContestCount());

        stats.getMetrics().put("maxRank", dashboard.getMaxRank());
        stats.getMetrics().put("contribution", dashboard.getContribution());
        stats.getMetrics().put("acceptedSubmissions", dashboard.getAcceptedSubmissionCount());
        stats.getMetrics().put("syncedSubmissions", dashboard.getSyncedSubmissionCount());
        stats.getMetrics().put("verdictCounts", dashboard.getVerdictCounts());
        stats.getMetrics().put("languageCounts", dashboard.getLanguageCounts());
        return stats;
    }

    private PlatformStatistics buildLeetCodeStatistics(PlatformAccount account) {
        PlatformStatistics stats = baseStatistics("LeetCode", account);

        if (!hasAccount(account)) {
            return stats;
        }

        try {
            LeetCodeProfileResponse profile =
                    leetCodeService.getProfile(account.getHandle().trim());

            stats.setAvailable(true);
            stats.setHandle(profile.getUsername());
            stats.setProfileUrl("https://leetcode.com/u/" + profile.getUsername() + "/");
            stats.setSolved(profile.getTotalSolved());
            stats.setContests(null);

            stats.getMetrics().put("ranking", profile.getRanking());
            stats.getMetrics().put("easySolved", profile.getEasySolved());
            stats.getMetrics().put("mediumSolved", profile.getMediumSolved());
            stats.getMetrics().put("hardSolved", profile.getHardSolved());
            stats.getMetrics().put("totalSubmissions", profile.getTotalSubmissions());
            stats.getMetrics().put("acceptedSubmissions", profile.getAcceptedSubmissions());
            stats.getMetrics().put("acceptanceRate", profile.getAcceptanceRate());
            return stats;

        } catch (RuntimeException exception) {
            markUnavailable(stats, exception);
            return stats;
        }
    }

    private PlatformStatistics buildCodeChefStatistics(PlatformAccount account) {
        PlatformStatistics stats = baseStatistics("CodeChef", account);

        if (!hasAccount(account)) {
            return stats;
        }

        try {
            CodeChefProfileResponse profile =
                    codeChefService.getProfile(account.getHandle().trim());

            stats.setAvailable(true);
            stats.setHandle(profile.getUsername());
            stats.setProfileUrl(profile.getProfileUrl());
            stats.setRating(profile.getCurrentRating());
            stats.setMaxRating(profile.getHighestRating());
            stats.setRank(profile.getGlobalRank());
            stats.setSolved(profile.getProblemsSolved());
            stats.setContests(profile.getContestsParticipated());

            stats.getMetrics().put("stars", profile.getStars());
            stats.getMetrics().put("countryRank", profile.getCountryRank());
            stats.getMetrics().put("country", profile.getCountry());
            return stats;

        } catch (RuntimeException exception) {
            markUnavailable(stats, exception);
            return stats;
        }
    }

    private PlatformStatistics buildAtCoderStatistics(PlatformAccount account) {
        PlatformStatistics stats = baseStatistics("AtCoder", account);

        if (!hasAccount(account)) {
            return stats;
        }

        try {
            AtCoderProfileResponse profile =
                    atCoderService.getProfile(account.getHandle().trim());

            stats.setAvailable(true);
            stats.setHandle(profile.getUsername());
            stats.setProfileUrl(profile.getProfileUrl());
            stats.setRating(profile.getRating());
            stats.setMaxRating(profile.getHighestRating());
            stats.setRank(profile.getRank());
            stats.setContests(profile.getRatedMatches());

            stats.getMetrics().put("lastCompeted", profile.getLastCompeted());
            stats.getMetrics().put("country", profile.getCountry());
            stats.getMetrics().put("affiliation", profile.getAffiliation());
            stats.getMetrics().put("winCount", profile.getWinCount());
            stats.getMetrics().put("ratedMatches", profile.getRatedMatches());
            return stats;

        } catch (RuntimeException exception) {
            markUnavailable(stats, exception);
            return stats;
        }
    }

    private PlatformStatistics baseStatistics(String platform, PlatformAccount account) {
        PlatformStatistics stats = new PlatformStatistics();
        stats.setPlatform(platform);
        stats.setConnected(hasAccount(account));
        stats.setAvailable(false);

        if (account != null) {
            stats.setHandle(account.getHandle());
            stats.setProfileUrl(account.getProfileUrl());
        }

        return stats;
    }

    private void markUnavailable(PlatformStatistics stats, RuntimeException exception) {
        stats.setAvailable(false);
        stats.setError(safeMessage(exception));
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? "Platform data could not be loaded."
                : message;
    }

    private boolean hasAccount(PlatformAccount account) {
        return account != null && isValidHandle(account.getHandle());
    }

    private boolean isValidHandle(String handle) {
        return handle != null && !handle.isBlank();
    }

    private boolean isFresh(long createdAt) {
        return System.currentTimeMillis() - createdAt < CACHE_TTL_MILLIS;
    }

    private DashboardResponse.TrackerStats buildTrackerStats(User user) {
        List<Problem> problems = problemRepository.findByUser(user);

        DashboardResponse.TrackerStats stats =
                new DashboardResponse.TrackerStats();

        int solved = 0;
        int attempted = 0;
        int unsolved = 0;
        int favorites = 0;

        HashSet<String> platforms = new HashSet<>();

        for (Problem problem : problems) {
            String status = problem.getStatus();

            if ("Solved".equalsIgnoreCase(status)) {
                solved++;
            } else if ("Attempted".equalsIgnoreCase(status)) {
                attempted++;
            } else if ("Unsolved".equalsIgnoreCase(status)) {
                unsolved++;
            }

            if (problem.isFavorite()) {
                favorites++;
            }

            if (problem.getPlatform() != null && !problem.getPlatform().isBlank()) {
                platforms.add(problem.getPlatform());
            }
        }

        stats.setTotalProblems(problems.size());
        stats.setSolvedProblems(solved);
        stats.setAttemptedProblems(attempted);
        stats.setUnsolvedProblems(unsolved);
        stats.setFavoriteProblems(favorites);
        stats.setPlatformCount(platforms.size());
        return stats;
    }

    private DashboardResponse.CodeforcesDashboard buildCodeforcesDashboard(String handle) {
        CodeforcesUserResponse profile = codeforcesService.getUser(handle);
        sleepBetweenRequests();
        List<CodeforcesRatingChangeResponse> ratingHistory =
                codeforcesService.getRatingHistory(handle);
        sleepBetweenRequests();
        CodeforcesSubmissionSummaryResponse submissions =
                codeforcesService.getSubmissionSummary(handle);

        DashboardResponse.CodeforcesDashboard dashboard =
                new DashboardResponse.CodeforcesDashboard();

        dashboard.setConnected(true);
        dashboard.setHandle(profile.getHandle());
        dashboard.setRating(profile.getRating());
        dashboard.setMaxRating(profile.getMaxRating());
        dashboard.setRank(profile.getRank());
        dashboard.setMaxRank(profile.getMaxRank());
        dashboard.setContribution(profile.getContribution());
        dashboard.setContestCount(ratingHistory.size());
        dashboard.setSolvedProblemCount(submissions.getSolvedProblemCount());
        dashboard.setAcceptedSubmissionCount(submissions.getAcceptedSubmissionCount());
        dashboard.setSyncedSubmissionCount(submissions.getSyncedSubmissionCount());
        dashboard.setVerdictCounts(submissions.getVerdictCounts());
        dashboard.setLanguageCounts(submissions.getLanguageCounts());
        dashboard.setRatingHistory(lastItems(ratingHistory, 30));
        dashboard.setRecentSubmissions(submissions.getRecentSubmissions());
        return dashboard;
    }

    private <T> List<T> lastItems(List<T> items, int limit) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        int start = Math.max(0, items.size() - limit);
        return new ArrayList<>(items.subList(start, items.size()));
    }

    private void sleepBetweenRequests() {
        try {
            Thread.sleep(CODEFORCES_REQUEST_GAP_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Dashboard synchronization was interrupted", exception);
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private static class CachedDashboard {
        private long createdAt;
        private DashboardResponse dashboard;
    }
}
