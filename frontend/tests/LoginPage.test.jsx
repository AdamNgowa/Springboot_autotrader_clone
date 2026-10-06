import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";

import LoginPage from "../src/pages/LoginPage";
import { useAuth } from "../src/hooks/useAuth";

// LoginPage talks to the world only through useAuth().login, so we mock the
// hook and decide what login does (resolve, reject, or stay pending).
vi.mock("../src/hooks/useAuth", () => ({
  useAuth: vi.fn(),
}));

describe("LoginPage", () => {
  let login;

  beforeEach(() => {
    vi.resetAllMocks();

    login = vi.fn();
    useAuth.mockReturnValue({
      login,
      isAuthenticated: false,
      loading: false,
    });
  });

  // MemoryRouter is required because the page contains a <Link>.
  function renderPage() {
    return render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );
  }

  function fillForm(email, password) {
    fireEvent.change(screen.getByLabelText("Email"), {
      target: { value: email },
    });
    fireEvent.change(screen.getByLabelText("Password"), {
      target: { value: password },
    });
  }

  // fireEvent.submit skips the browser's own constraint validation, so these
  // tests exercise OUR validation (validateLogin) rather than the HTML one.
  function submitForm() {
    fireEvent.submit(
      screen.getByRole("button", { name: /login|signing in/i }).closest("form"),
    );
  }

  it("renders the email field, password field and login button", () => {
    renderPage();

    expect(screen.getByLabelText("Email")).toBeInTheDocument();
    expect(screen.getByLabelText("Password")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Login" })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Register" })).toHaveAttribute(
      "href",
      "/register",
    );
  });

  it("shows validation errors and does not call login when the form is empty", () => {
    renderPage();

    submitForm();

    expect(screen.getByText("Email is required")).toBeInTheDocument();
    expect(screen.getByText("Password is required")).toBeInTheDocument();
    expect(login).not.toHaveBeenCalled();
  });

  it("clears a field's validation error as soon as the user types in it", () => {
    renderPage();
    submitForm();

    fireEvent.change(screen.getByLabelText("Email"), {
      target: { value: "john@example.com" },
    });

    expect(screen.queryByText("Email is required")).not.toBeInTheDocument();
    // The untouched field keeps its error.
    expect(screen.getByText("Password is required")).toBeInTheDocument();
  });

  it("calls login with the entered credentials", async () => {
    login.mockResolvedValue({ token: "jwt" });
    renderPage();

    fillForm("john@example.com", "SecurePassword123!");
    submitForm();

    await waitFor(() => {
      expect(login).toHaveBeenCalledWith({
        email: "john@example.com",
        password: "SecurePassword123!",
      });
    });
  });

  it("disables the form and shows progress while the request is pending", async () => {
    // A promise we resolve by hand, so the "in flight" state stays observable.
    let resolveLogin;
    login.mockReturnValue(
      new Promise((resolve) => {
        resolveLogin = resolve;
      }),
    );
    renderPage();

    fillForm("john@example.com", "SecurePassword123!");
    submitForm();

    const pendingButton = await screen.findByRole("button", {
      name: "Signing in...",
    });
    expect(pendingButton).toBeDisabled();
    expect(screen.getByLabelText("Email")).toBeDisabled();
    expect(screen.getByLabelText("Password")).toBeDisabled();

    resolveLogin({ token: "jwt" });

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Login" })).toBeEnabled();
    });
  });

  it("shows the server error message when login fails and re-enables the form", async () => {
    login.mockRejectedValue(new Error("Invalid email or password"));
    renderPage();

    fillForm("john@example.com", "WrongPassword123!");
    submitForm();

    const alert = await screen.findByRole("alert");
    expect(alert).toHaveTextContent("Invalid email or password");
    expect(screen.getByRole("button", { name: "Login" })).toBeEnabled();
    expect(screen.getByLabelText("Email")).toBeEnabled();
  });

  it("shows a loading message while the session is restoring", () => {
    useAuth.mockReturnValue({ login, isAuthenticated: false, loading: true });

    renderPage();

    expect(screen.getByText("Loading...")).toBeInTheDocument();
    expect(screen.queryByLabelText("Email")).not.toBeInTheDocument();
  });

  it("renders nothing for an already authenticated user", () => {
    useAuth.mockReturnValue({ login, isAuthenticated: true, loading: false });

    const { container } = renderPage();

    // Redirecting away is GuestOnlyRoute's job; the page itself just renders nothing.
    expect(container).toBeEmptyDOMElement();
  });
});
