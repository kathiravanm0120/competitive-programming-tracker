package Competitive.Programming.Tracker.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "TestOnlySecretKeyForCompetitiveProgrammingTracker2026";

    @Test
    void generateAndExtractUsername() {
        JwtService jwtService = new JwtService(SECRET, 3_600_000);

        String token = jwtService.generateToken("testuser");

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void rejectsBlankUsername() {
        JwtService jwtService = new JwtService(SECRET, 3_600_000);

        assertThrows(IllegalArgumentException.class,
                () -> jwtService.generateToken(" "));
    }

    @Test
    void rejectsTamperedToken() {
        JwtService jwtService = new JwtService(SECRET, 3_600_000);
        String token = jwtService.generateToken("testuser");
        String tampered = token.substring(0, token.length() - 1) + "x";

        assertThrows(Exception.class,
                () -> jwtService.extractUsername(tampered));
    }
}
