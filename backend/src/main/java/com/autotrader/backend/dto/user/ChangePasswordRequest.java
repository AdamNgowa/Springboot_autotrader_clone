package com.autotrader.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    // Only checked for presence here. Whether it is CORRECT is a business rule,
    // decided in UserService against the stored hash.
    @NotBlank(message = "Current password is required")
    @Schema(description = "The account's current password")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100,
            message = "Password must be between 8 and 100 characters")
    @Schema(description = "The new password", example = "NewSecurePassword123!")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}