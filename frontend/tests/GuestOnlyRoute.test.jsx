import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";

import GuestOnlyRoute from "../src/components/GuestOnlyRoute";
import { useAuth } from "../src/hooks/useAuth";

vi.mock("../src/hooks/useAuth", () => ({
  useAuth: vi.fn(),
}));

// initialEntry may be a plain path or an object carrying router state,
// which is how ProtectedRoute hands "where you came from" to the login page.
function renderAt(initialEntry) {
  return render(
    <MemoryRouter initialEntries={[initialEntry]}>
      <Routes>
        <Route path="/" element={<p>Home page</p>} />
        <Route path="/dashboard" element={<p>Dashboard page</p>} />
        <Route
          path="/login"
          element={
            <GuestOnlyRoute>
              <p>Login form</p>
            </GuestOnlyRoute>
          }
        />
      </Routes>
    </MemoryRouter>,
  );
}

describe("GuestOnlyRoute", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("shows a loading message while the session is restoring", () => {
    useAuth.mockReturnValue({ loading: true, isAuthenticated: false });

    renderAt("/login");

    expect(screen.getByText("Loading...")).toBeInTheDocument();
    expect(screen.queryByText("Login form")).not.toBeInTheDocument();
    expect(screen.queryByText("Home page")).not.toBeInTheDocument();
  });

  it("renders the guest page for an unauthenticated user", () => {
    useAuth.mockReturnValue({ loading: false, isAuthenticated: false });

    renderAt("/login");

    expect(screen.getByText("Login form")).toBeInTheDocument();
  });

  it("redirects an authenticated user to the home page by default", () => {
    useAuth.mockReturnValue({ loading: false, isAuthenticated: true });

    renderAt("/login");

    expect(screen.getByText("Home page")).toBeInTheDocument();
    expect(screen.queryByText("Login form")).not.toBeInTheDocument();
  });

  it("sends an authenticated user back to the route they originally requested", () => {
    useAuth.mockReturnValue({ loading: false, isAuthenticated: true });

    renderAt({
      pathname: "/login",
      state: { from: { pathname: "/dashboard" } },
    });

    expect(screen.getByText("Dashboard page")).toBeInTheDocument();
    expect(screen.queryByText("Home page")).not.toBeInTheDocument();
  });
});
