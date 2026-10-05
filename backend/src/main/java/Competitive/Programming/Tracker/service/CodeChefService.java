package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.CodeChefProfileResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeChefService {

    private static final String PROFILE_URL = "https://www.codechef.com/users/";

    private static final Pattern CONTEST_COUNT_PATTERN =
            Pattern.compile("No\\. of Contests Participated\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern SOLVED_PATTERN =
            Pattern.compile("Total Problems Solved\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern HIGHEST_RATING_PATTERN =
            Pattern.compile("CodeChef Rating \\(Highest Rating (\\d+)\\)", Pattern.CASE_INSENSITIVE);

    private static final Pattern GLOBAL_RANK_PATTERN =
            Pattern.compile("Global Rank\\s*:\\s*([^\\n]+?)(?=Country Rank|Cheated in|Ratings System|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern COUNTRY_RANK_PATTERN =
            Pattern.compile("Country Rank\\s*:\\s*([^\\n]+?)(?=CodeChef Rating|Cheated in|Ratings System|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern RATING_PATTERN =
            Pattern.compile("\\b(\\d{3,4})\\s*\\([^)]*[+-]\\d+[^)]*\\)\\s*Rating", Pattern.CASE_INSENSITIVE);

    private static final Pattern STAR_PATTERN =
            Pattern.compile("\\b([1-7])\\s*(?:\\*{1,7}|★)", Pattern.CASE_INSENSITIVE);

    public CodeChefProfileResponse getProfile(String username) {
        String normalizedUsername = normalizeUsername(username);
        String profileUrl = PROFILE_URL + normalizedUsername;

        try {
            Document document = Jsoup.connect(profileUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/142.0 Safari/537.36")
                    .referrer("https://www.google.com/")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            String bodyText = document.body() == null
                    ? ""
                    : document.body().text();

            if (bodyText.isBlank() || bodyText.toLowerCase().contains("page not found")) {
                throw new IllegalArgumentException(
                        "CodeChef user not found: " + normalizedUsername);
            }

            CodeChefProfileResponse response = new CodeChefProfileResponse();
            response.setUsername(normalizedUsername);
            response.setProfileUrl(profileUrl);
            response.setCurrentRating(extractCurrentRating(document, bodyText));
            response.setHighestRating(extractInteger(HIGHEST_RATING_PATTERN, bodyText));
            response.setStars(extractStars(document, bodyText));
            response.setGlobalRank(cleanRank(extractFirst(GLOBAL_RANK_PATTERN, bodyText)));
            response.setCountryRank(cleanRank(extractFirst(COUNTRY_RANK_PATTERN, bodyText)));
            response.setContestsParticipated(extractInteger(CONTEST_COUNT_PATTERN, bodyText));
            response.setProblemsSolved(extractInteger(SOLVED_PATTERN, bodyText));
            response.setCountry(extractCountry(document));

            return response;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not connect to CodeChef. The public profile page may be temporarily unavailable.",
                    exception);
        }
    }

    private Integer extractCurrentRating(Document document, String bodyText) {
        Elements ratingElements = document.select(".rating-number");

        for (Element element : ratingElements) {
            String text = element.text().replaceAll("[^0-9]", "").trim();
            if (!text.isBlank()) {
                try {
                    int value = Integer.parseInt(text);
                    if (value >= 0 && value <= 5000) {
                        return value;
                    }
                } catch (NumberFormatException ignored) {
                    // Try the text-based fallback below.
                }
            }
        }

        Matcher matcher = RATING_PATTERN.matcher(bodyText);
        if (matcher.find()) {
            return parseInteger(matcher.group(1));
        }

        return null;
    }

    private Integer extractStars(Document document, String bodyText) {
        Elements starElements = document.select(".rating-star, .rating-star span");

        for (Element element : starElements) {
            Matcher matcher = STAR_PATTERN.matcher(element.text());
            if (matcher.find()) {
                return parseInteger(matcher.group(1));
            }
        }

        Matcher matcher = STAR_PATTERN.matcher(bodyText);
        if (matcher.find()) {
            return parseInteger(matcher.group(1));
        }

        return null;
    }

    private String extractCountry(Document document) {
        for (Element element : document.select("img[alt]")) {
            String alt = element.attr("alt").trim();
            if (alt.length() >= 2 && alt.length() <= 60 && !alt.equalsIgnoreCase("image")) {
                return alt;
            }
        }
        return null;
    }

    private String extractFirst(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Integer extractInteger(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? parseInteger(matcher.group(1)) : null;
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (Exception exception) {
            return null;
        }
    }

    private String cleanRank(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value
                .replaceAll("\\s+", " ")
                .replaceAll("[|•]+$", "")
                .trim();

        return cleaned.isBlank() ? null : cleaned;
    }

    private String normalizeUsername(String username) {
        String normalized = username == null ? "" : username.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("CodeChef username is required");
        }

        return normalized;
    }
}
