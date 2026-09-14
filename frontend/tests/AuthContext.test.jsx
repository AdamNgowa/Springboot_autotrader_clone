import { render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { AuthProvider } from "../src/context/AuthContext";
import { useAuth } from "../src/hooks/useAuth";

vi.mock("../api/userApi", () => ({
  getCurrentUser: vi.fn(),
}));

vi.mock("../api/authApi", () => ({
  login: vi.fn(),
  register: vi.fn(),
}));

function AuthConsumer() {
  const { loading, isAuthenticated, token, user } = useAuth();

  return (
    <div>
      <span data-testid="loading">{String(loading)}</span>
      <span data-testid="authenticated">{String(isAuthenticated)}</span>
      <span data-testid="token">{String(token)}</span>
      <span data-testid="user">{String(user)}</span>
    </div>
  );
}

describe("AuthProvider", () => {
  it("initializes as unauthenticated when no token is stored", async () => {
    localStorage.clear();

    render(
      <AuthProvider>
        <AuthConsumer />
      </AuthProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId("loading")).toHaveTextContent("false");
    });

    expect(screen.getByTestId("authenticated")).toHaveTextContent("false");
    expect(screen.getByTestId("token")).toHaveTextContent("null");
    expect(screen.getByTestId("user")).toHaveTextContent("null");
  });
});
