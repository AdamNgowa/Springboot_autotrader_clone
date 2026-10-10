package com.autotrader.backend.service;

import com.autotrader.backend.dto.user.ChangePasswordRequest;
import com.autotrader.backend.dto.user.UpdateProfileRequest;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.IncorrectCurrentPasswordException;
import com.autotrader.backend.exception.UserNotFoundException;
import com.autotrader.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_existingUser_returnsUser() {
        // Arrange
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertThat(result).isSameAs(user);
    }

    @Test
    void getUserById_unknownUser_throwsUserNotFoundException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    // ==========================================
    // updateProfile
    // ==========================================

    @Test
    void updateProfile_updatesAndTrimsEditableFields() {
        // Arrange: the caller holds a detached copy; the service reloads the row by id.
        User authenticated = mock(User.class);
        when(authenticated.getId()).thenReturn(1L);

        User stored = new User();
        stored.setEmail("john@example.com");
        stored.setFirstName("Old");
        stored.setLastName("Name");
        stored.setPhoneNumber("0700000000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(stored));
        when(userRepository.save(stored)).thenReturn(stored);

        UpdateProfileRequest request =
                new UpdateProfileRequest("  Jane ", " Smith  ", " 0712345678 ");

        // Act
        User result = userService.updateProfile(authenticated, request);

        // Assert
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getPhoneNumber()).isEqualTo("0712345678");
        // Email must never change through this method.
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    // ==========================================
    // changePassword
    // ==========================================

    @Test
    void changePassword_correctCurrentPassword_storesEncodedNewPassword() {
        // Arrange
        User authenticated = mock(User.class);
        when(authenticated.getId()).thenReturn(1L);

        User stored = new User();
        stored.setPassword("old-hash");

        when(userRepository.findById(1L)).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("current123", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("newPassword1")).thenReturn("new-hash");

        // Act
        userService.changePassword(
                authenticated,
                new ChangePasswordRequest("current123", "newPassword1"));

        // Assert: the HASH is stored, never the plain text.
        assertThat(stored.getPassword()).isEqualTo("new-hash");
        verify(userRepository).save(stored);
    }

    @Test
    void changePassword_wrongCurrentPassword_throwsAndChangesNothing() {
        // Arrange
        User authenticated = mock(User.class);
        when(authenticated.getId()).thenReturn(1L);

        User stored = new User();
        stored.setPassword("old-hash");

        when(userRepository.findById(1L)).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> userService.changePassword(
                authenticated,
                new ChangePasswordRequest("wrong", "newPassword1")))
                .isInstanceOf(IncorrectCurrentPasswordException.class)
                .hasMessage("Current password is incorrect");

        assertThat(stored.getPassword()).isEqualTo("old-hash");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}