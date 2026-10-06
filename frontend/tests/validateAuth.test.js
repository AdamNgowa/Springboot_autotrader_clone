import { describe, expect, it } from "vitest";

import { validateLogin, validateRegister } from "../src/utils/validateAuth";

// Pure functions: no rendering, no mocks. Input object in, errors object out.

describe("validateLogin", () => {
  const validCredentials = () => ({
    email: "john@example.com",
    password: "SecurePassword123!",
  });

  it("returns no errors for valid credentials", () => {
    expect(validateLogin(validCredentials())).toEqual({});
  });

  it("requires an email", () => {
    const errors = validateLogin({ ...validCredentials(), email: "" });

    expect(errors).toEqual({ email: "Email is required" });
  });

  it("treats a whitespace-only email as empty", () => {
    const errors = validateLogin({ ...validCredentials(), email: "   " });

    expect(errors.email).toBe("Email is required");
  });

  it("requires a password", () => {
    const errors = validateLogin({ ...validCredentials(), password: "" });

    expect(errors).toEqual({ password: "Password is required" });
  });

  it("reports both errors at once", () => {
    const errors = validateLogin({ email: "", password: "" });

    expect(errors).toEqual({
      email: "Email is required",
      password: "Password is required",
    });
  });
});

describe("validateRegister", () => {
  const validUser = () => ({
    firstName: "John",
    lastName: "Doe",
    email: "john@example.com",
    password: "SecurePassword123!",
    phoneNumber: "+254712345678",
  });

  it("returns no errors for a valid user", () => {
    expect(validateRegister(validUser())).toEqual({});
  });

  it.each([
    ["firstName", "First name is required"],
    ["lastName", "Last name is required"],
    ["email", "Email is required"],
    ["password", "Password is required"],
    ["phoneNumber", "Phone number is required"],
  ])("requires %s", (field, message) => {
    const errors = validateRegister({ ...validUser(), [field]: "" });

    expect(errors).toEqual({ [field]: message });
  });

  it.each(["firstName", "lastName", "email", "phoneNumber"])(
    "treats a whitespace-only %s as empty",
    (field) => {
      const errors = validateRegister({ ...validUser(), [field]: "   " });

      expect(errors[field]).toBeDefined();
    },
  );

  it("rejects a password shorter than 8 characters", () => {
    const errors = validateRegister({ ...validUser(), password: "short12" });

    expect(errors).toEqual({
      password: "Password must be at least 8 characters",
    });
  });

  it("accepts a password of exactly 8 characters", () => {
    const errors = validateRegister({ ...validUser(), password: "exactly8" });

    expect(errors).toEqual({});
  });

  it("reports every missing field at once", () => {
    const errors = validateRegister({
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      phoneNumber: "",
    });

    expect(Object.keys(errors).sort()).toEqual([
      "email",
      "firstName",
      "lastName",
      "password",
      "phoneNumber",
    ]);
  });
});
