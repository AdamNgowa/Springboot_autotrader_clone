import { describe, expect, it } from "vitest";

import {
    validatePasswordChange,
    validateProfile,
} from "../src/utils/validateAuth";

describe("validateProfile", () => {
    const valid = {
        firstName: "Jane",
        lastName: "Smith",
        phoneNumber: "+254712345678",
    };

    it("returns no errors for valid values", () => {
        expect(validateProfile(valid)).toEqual({});
    });

    it("requires every field, ignoring surrounding spaces", () => {
        const errors = validateProfile({
            firstName: "  ",
            lastName: "",
            phoneNumber: " ",
        });

        expect(errors.firstName).toBe("First name is required");
        expect(errors.lastName).toBe("Last name is required");
        expect(errors.phoneNumber).toBe("Phone number is required");
    });

    it("rejects names longer than 50 characters", () => {
        const errors = validateProfile({ ...valid, firstName: "a".repeat(51) });

        expect(errors.firstName).toBe("First name must not exceed 50 characters");
    });
});

describe("validatePasswordChange", () => {
    const valid = {
        currentPassword: "OldPassword1!",
        newPassword: "NewPassword2!",
        confirmPassword: "NewPassword2!",
    };

    it("returns no errors for valid values", () => {
        expect(validatePasswordChange(valid)).toEqual({});
    });

    it("requires all three fields", () => {
        const errors = validatePasswordChange({
            currentPassword: "",
            newPassword: "",
            confirmPassword: "",
        });

        expect(errors.currentPassword).toBeDefined();
        expect(errors.newPassword).toBeDefined();
        expect(errors.confirmPassword).toBeDefined();
    });

    it("rejects a new password shorter than 8 characters", () => {
        const errors = validatePasswordChange({
            ...valid,
            newPassword: "short",
            confirmPassword: "short",
        });

        expect(errors.newPassword).toBe("Password must be at least 8 characters");
    });

    it("rejects a new password equal to the current one", () => {
        const errors = validatePasswordChange({
            ...valid,
            newPassword: valid.currentPassword,
            confirmPassword: valid.currentPassword,
        });

        expect(errors.newPassword).toMatch(/different/);
    });

    it("rejects a mismatched confirmation", () => {
        const errors = validatePasswordChange({
            ...valid,
            confirmPassword: "Different3!",
        });

        expect(errors.confirmPassword).toBe("Passwords do not match");
    });
});