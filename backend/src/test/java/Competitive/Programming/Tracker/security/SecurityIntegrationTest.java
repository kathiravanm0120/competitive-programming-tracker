package Competitive.Programming.Tracker.security;

import Competitive.Programming.Tracker.entity.Role;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRequiresJwt() throws Exception {
        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsPreflightIsAllowed() throws Exception {
        mockMvc.perform(
                options("/api/problems")
                        .header(
                                "Origin",
                                "http://localhost:5173")
                        .header(
                                "Access-Control-Request-Method",
                                "GET")
                        .header(
                                "Access-Control-Request-Headers",
                                "authorization"))
                .andExpect(status().isOk());
    }

    @Test
    void registrationRejectsWeakPassword() throws Exception {

        String body = "{\"username\":\"user123\",\"email\":\"user@example.com\",\"password\":\"1234567\"}";

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void normalUserCannotAccessAdminApi() throws Exception {

        User user = new User(
                "normaluser",
                "normal@example.com",
                passwordEncoder.encode("password123"));

        user.setRole(Role.USER);

        userRepository.save(user);

        String token = jwtService.generateToken("normaluser");

        mockMvc.perform(
                get("/api/admin/stats")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminApi() throws Exception {

        User admin = new User(
                "adminuser",
                "admin@example.com",
                passwordEncoder.encode("password123"));

        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        String token = jwtService.generateToken("adminuser");

        mockMvc.perform(
                get("/api/admin/stats")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void disabledUserCannotAccessProtectedApi() throws Exception {

        User user = new User(
                "disableduser",
                "disabled@example.com",
                passwordEncoder.encode("password123"));

        user.setEnabled(false);

        userRepository.save(user);

        String token = jwtService.generateToken("disableduser");

        mockMvc.perform(
                get("/api/problems")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}