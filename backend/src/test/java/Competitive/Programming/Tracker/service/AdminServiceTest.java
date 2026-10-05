package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.entity.Role;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock UserRepository userRepository;
    @Mock ProblemRepository problemRepository;
    @Mock PlatformAccountRepository platformAccountRepository;
    @Mock GoalRepository goalRepository;
    @Mock PlannerTaskRepository plannerTaskRepository;
    @Mock NotificationRepository notificationRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(
                userRepository,
                problemRepository,
                platformAccountRepository,
                goalRepository,
                plannerTaskRepository,
                notificationRepository);
    }

    @Test
    void adminCannotDisableOwnAccount() {
        User admin = new User("admin", "admin@example.com", "HASHED");
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalStateException.class,
                () -> adminService.setUserEnabled(1L, false, "admin"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void adminCannotRemoveOwnAdminRole() {
        User admin = new User("admin", "admin@example.com", "HASHED");
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalStateException.class,
                () -> adminService.setUserRole(1L, "USER", "admin"));

        verify(userRepository, never()).save(any(User.class));
    }
}
