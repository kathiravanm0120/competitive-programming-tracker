package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.AtCoderProfileResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AtCoderService {

    private static final String PROFILE_URL = "https://atcoder.jp/users/";

    private static final Pattern RANK_PATTERN = Pattern.compile(
            "Rank\\s*\\|\\s*([^|]+?)\\s+Rating",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern RATING_PATTERN = Pattern.compile(
            "Rating\\s*\\|\\s*([^\\s|]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern HIGHEST_RATING_PATTERN = Pattern.compile(
            "Highest Rating\\s*\\|\\s*([0-9,]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern RATED_MATCHES_PATTERN = Pattern.compile(
            "Rated Matches\\s*\\|\\s*([0-9,]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LAST_COMPETED_PATTERN = Pattern.compile(
            "Last Competed\\s*\\|\\s*([^|]+?)\\s*(?=Rating|$)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern COUNTRY_PATTERN = Pattern.compile(
            "Country/Region\\s*\\|\\s*([^|]+?)\\s+Birth Year",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern AFFILIATION_PATTERN = Pattern.compile(
            "Affiliation\\s*\\|\\s*([^|]+?)(?=Win|Contest Status|$)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern WIN_PATTERN = Pattern.compile(
            "Win\\s+([0-9,]+)",
            Pattern.CASE_INSENSITIVE
    );

    public AtCoderProfileResponse getProfile(String username) {
        String normalizedUsername = normalizeUsername(username);
        String profileUrl = PROFILE_URL + normalizedUsername;

        try {
            Document document = Jsoup.connect(profileUrl)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/142.0 Safari/537.36"
                    )
                    .referrer("https://www.google.com/")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            String bodyText = document.body() == null
                    ? ""
                    : document.body().text();

            String title = document.title() == null ? "" : document.title();
            if (bodyText.isBlank() || title.toLowerCase().contains("error") ||
                    bodyText.toLowerCase().contains("user not found")) {
                throw new IllegalArgumentException(
                        "AtCoder user not found: " + normalizedUsername
                );
            }

            AtCoderProfileResponse response = new AtCoderProfileResponse();
            response.setUsername(normalizedUsername);
            response.setProfileUrl(profileUrl);
            response.setRank(clean(extractFirst(RANK_PATTERN, bodyText)));
            response.setRating(extractInteger(RATING_PATTERN, bodyText));
            response.setHighestRating(extractInteger(HIGHEST_RATING_PATTERN, bodyText));
            response.setRatedMatches(extractInteger(RATED_MATCHES_PATTERN, bodyText));
            response.setLastCompeted(clean(extractFirst(LAST_COMPETED_PATTERN, bodyText)));
            response.setCountry(clean(extractFirst(COUNTRY_PATTERN, bodyText)));
            response.setAffiliation(clean(extractFirst(AFFILIATION_PATTERN, bodyText)));
            response.setWinCount(extractInteger(WIN_PATTERN, bodyText));

            if (response.getRating() == null && response.getHighestRating() == null) {
                throw new IllegalArgumentException(
                        "AtCoder profile could not be parsed for user: " + normalizedUsername
                );
            }

            return response;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not connect to AtCoder. The public profile page may be temporarily unavailable.",
                    exception
            );
        }
    }

    private String extractFirst(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Integer extractInteger(Pattern pattern, String text) {
        String value = extractFirst(pattern, text);
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value
                .replaceAll("\\s+", " ")
                .trim();

        return cleaned.isBlank() ? null : cleaned;
    }

    private String normalizeUsername(String username) {
        String normalized = username == null ? "" : username.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("AtCoder username is required");
        }

        return normalized;
    }
}
