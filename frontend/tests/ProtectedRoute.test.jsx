import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";

import ProtectedRoute from "../src/components/ProtectedRoute";
import { useAuth } from "../src/hooks/useAuth";

// ProtectedRoute only READS auth state, so we replace useAuth and drive each
// scenario (loading / anonymous / logged in) directly. No provider, no API.
vi.mock("../src/hooks/useAuth", () => ({
  useAuth: vi.fn(),
}));

// Stand-in login page that reveals where the user was redirected FROM.
function LoginProbe() {
  const location = useLocation();

  return (
    <p>{`Login page, came from: ${location.state?.from?.pathname ?? "nowhere"}`}</p>
  );
}

function renderAt(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/login" element={<LoginProbe />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <p>Secret dashboard</p>
            </ProtectedRoute>
          }
        />
      </Routes>
    </MemoryRouter>,
  );
}

describe("ProtectedRoute", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("shows a loading message and does not redirect while the session is restoring", () => {
    useAuth.mockReturnValue({ loading: true, isAuthenticated: false });

    renderAt("/dashboard");

    expect(screen.getByText("Loading...")).toBeInTheDocument();
    expect(screen.queryByText("Secret dashboard")).not.toBeInTheDocument();
    expect(screen.queryByText(/Login page/)).not.toBeInTheDocument();
  });

  it("redirects an unauthenticated user to /login and remembers the requested route", () => {
    useAuth.mockReturnValue({ loading: false, isAuthenticated: false });

    renderAt("/dashboard");

    expect(
      screen.getByText("Login page, came from: /dashboard"),
    ).toBeInTheDocument();
    expect(screen.queryByText("Secret dashboard")).not.toBeInTheDocument();
  });

  it("renders the protected content for an authenticated user", () => {
    useAuth.mockReturnValue({ loading: false, isAuthenticated: true });

    renderAt("/dashboard");

    expect(screen.getByText("Secret dashboard")).toBeInTheDocument();
    expect(screen.queryByText(/Login page/)).not.toBeInTheDocument();
  });
});
