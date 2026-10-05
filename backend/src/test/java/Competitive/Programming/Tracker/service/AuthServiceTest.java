package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.LoginRequest;
import Competitive.Programming.Tracker.dto.RegisterRequest;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.UserRepository;
import Competitive.Programming.Tracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerHashesPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("NEWUSER@example.com");
        request.setPassword("strongpass");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("strongpass")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = authService.register(request);

        assertEquals("newuser", saved.getUsername());
        assertEquals("newuser@example.com", saved.getEmail());
        assertEquals("HASHED", saved.getPassword());
        verify(passwordEncoder).encode("strongpass");
    }

    @Test
    void loginRejectsDisabledUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = new User("testuser", "test@example.com", "HASHED");
        user.setEnabled(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = new User("testuser", "test@example.com", "HASHED");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "HASHED")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("jwt-token");

        assertEquals("jwt-token", authService.login(request));
    }
}
