import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { useState } from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { login as loginApi, register as registerApi } from "../src/api/authApi";
import { getCurrentUser } from "../src/api/userApi";
import { getToken, saveToken } from "../src/auth/authStorage";
import { AuthProvider } from "../src/context/AuthContext";
import { useAuth } from "../src/hooks/useAuth";

/*
 * IMPORTANT: vi.mock paths are resolved relative to THIS file, so they must
 * point into ../src/api. (The earlier "../api/userApi" pointed at a folder
 * that does not exist, so the mocks silently did nothing.)
 *
 * authStorage is deliberately NOT mocked: it is a thin wrapper over
 * localStorage, and jsdom provides a real one, so we test the true behaviour.
 */
vi.mock("../src/api/userApi", () => ({
    getCurrentUser: vi.fn(),
}));

vi.mock("../src/api/authApi", () => ({
    login: vi.fn(),
    register: vi.fn(),
}));

const testUser = { id: 1, email: "john@example.com", firstName: "John" };

// Exposes everything the provider offers, plus buttons to trigger its actions.
function AuthConsumer() {
    const { loading, isAuthenticated, token, user, login, register, logout } =
        useAuth();
    const [error, setError] = useState("none");

    async function run(action) {
        try {
            await action();
        } catch (caught) {
            setError(caught.message);
        }
    }

    return (
        <div>
            <span data-testid="loading">{String(loading)}</span>
            <span data-testid="authenticated">{String(isAuthenticated)}</span>
            <span data-testid="token">{String(token)}</span>
            <span data-testid="user">{user ? user.email : "null"}</span>
            <span data-testid="error">{error}</span>

            <button
                onClick={() =>
                    run(() => login({ email: "john@example.com", password: "pw" }))
                }
            >
                do-login
            </button>
            <button onClick={() => run(() => register({ email: "john@example.com" }))}>
                do-register
            </button>
            <button onClick={logout}>do-logout</button>
        </div>
    );
}

async function renderProvider() {
    render(
        <AuthProvider>
            <AuthConsumer />
        </AuthProvider>,
    );

    // Session restoration is asynchronous; wait until it has finished.
    await waitFor(() => {
        expect(screen.getByTestId("loading")).toHaveTextContent("false");
    });
}

describe("AuthProvider", () => {
    beforeEach(() => {
        localStorage.clear();
        vi.resetAllMocks();
    });

    describe("session restoration on startup", () => {
        it("initializes as unauthenticated when no token is stored", async () => {
            await renderProvider();

            expect(screen.getByTestId("authenticated")).toHaveTextContent("false");
            expect(screen.getByTestId("token")).toHaveTextContent("null");
            expect(screen.getByTestId("user")).toHaveTextContent("null");
            expect(getCurrentUser).not.toHaveBeenCalled();
        });

        it("restores the session when a stored token is valid", async () => {
            saveToken("stored-jwt");
            getCurrentUser.mockResolvedValue(testUser);

            await renderProvider();

            expect(screen.getByTestId("authenticated")).toHaveTextContent("true");
            expect(screen.getByTestId("token")).toHaveTextContent("stored-jwt");
            expect(screen.getByTestId("user")).toHaveTextContent("john@example.com");
        });

        it("discards a stored token the backend rejects", async () => {
            saveToken("stale-jwt");
            getCurrentUser.mockRejectedValue(new Error("Unauthorized"));

            await renderProvider();

            expect(screen.getByTestId("authenticated")).toHaveTextContent("false");
            expect(screen.getByTestId("token")).toHaveTextContent("null");
            expect(getToken()).toBeNull();
        });
    });

    describe("login", () => {
        it("stores the token and loads the user after a successful login", async () => {
            loginApi.mockResolvedValue({ token: "new-jwt" });
            getCurrentUser.mockResolvedValue(testUser);
            await renderProvider();

            fireEvent.click(screen.getByText("do-login"));

            await waitFor(() => {
                expect(screen.getByTestId("authenticated")).toHaveTextContent("true");
            });
            expect(screen.getByTestId("token")).toHaveTextContent("new-jwt");
            expect(screen.getByTestId("user")).toHaveTextContent("john@example.com");
            expect(getToken()).toBe("new-jwt");
            expect(loginApi).toHaveBeenCalledWith({
                email: "john@example.com",
                password: "pw",
            });
        });

        it("stays logged out and stores nothing when the credentials are rejected", async () => {
            loginApi.mockRejectedValue(new Error("Invalid email or password"));
            await renderProvider();

            fireEvent.click(screen.getByText("do-login"));

            await waitFor(() => {
                expect(screen.getByTestId("error")).toHaveTextContent(
                    "Invalid email or password",
                );
            });
            expect(screen.getByTestId("authenticated")).toHaveTextContent("false");
            expect(getToken()).toBeNull();
            expect(getCurrentUser).not.toHaveBeenCalled();
        });

        it("rolls the session back when the token is issued but the profile cannot be loaded", async () => {
            loginApi.mockResolvedValue({ token: "new-jwt" });
            getCurrentUser.mockRejectedValue(new Error("Profile unavailable"));
            await renderProvider();

            fireEvent.click(screen.getByText("do-login"));

            await waitFor(() => {
                expect(screen.getByTestId("error")).toHaveTextContent(
                    "Profile unavailable",
                );
            });
            // Neither React state nor localStorage may keep a half-established session.
            expect(screen.getByTestId("authenticated")).toHaveTextContent("false");
            expect(screen.getByTestId("token")).toHaveTextContent("null");
            expect(getToken()).toBeNull();
        });
    });

    describe("register", () => {
        it("authenticates the user automatically after registration", async () => {
            registerApi.mockResolvedValue({ token: "registered-jwt" });
            getCurrentUser.mockResolvedValue(testUser);
            await renderProvider();

            fireEvent.click(screen.getByText("do-register"));

            await waitFor(() => {
                expect(screen.getByTestId("authenticated")).toHaveTextContent("true");
            });
            expect(getToken()).toBe("registered-jwt");
        });
    });

    describe("logout", () => {
        it("clears the token from state and storage", async () => {
            saveToken("stored-jwt");
            getCurrentUser.mockResolvedValue(testUser);
            await renderProvider();
            expect(screen.getByTestId("authenticated")).toHaveTextContent("true");

            fireEvent.click(screen.getByText("do-logout"));

            await waitFor(() => {
                expect(screen.getByTestId("authenticated")).toHaveTextContent("false");
            });
            expect(screen.getByTestId("token")).toHaveTextContent("null");
            expect(screen.getByTestId("user")).toHaveTextContent("null");
            expect(getToken()).toBeNull();
        });
    });
});
