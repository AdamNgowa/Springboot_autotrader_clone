import { useState } from "react";
import { Navigate, Link, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { validateLogin } from "../utils/validateAuth";

function LoginPage() {
  const { login, isAuthenticated, loading } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loginError, setLoginError] = useState("");
  const [validationErrors, setValidationErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  if (loading) {
    return <p className="p-6 text-center">Loading...</p>;
  }

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const credentials = {
      email,
      password,
    };

    const errors = validateLogin(credentials);

    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }

    setValidationErrors({});
    setLoginError("");

    try {
      setSubmitting(true);
      await login(credentials);
    } catch (error) {
      setLoginError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="mx-auto mt-10 max-w-md rounded-lg border bg-white p-8 shadow">
      <h1 className="mb-6 text-center text-3xl font-bold">Welcome Back</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Email */}
        <label htmlFor="email" className="block text-sm font-medium">
          Email
        </label>
        <input
          id="email"
          type="email"
          placeholder="Email"
          value={email}
          disabled={submitting}
          onChange={(e) => {
            setEmail(e.target.value);
            setValidationErrors((current) => ({
              ...current,
              email: "",
            }));
          }}
          aria-invalid={Boolean(validationErrors.email)}
          aria-describedby={validationErrors.email ? "email-error" : undefined}
          className="w-full rounded border p-3"
        />
        {validationErrors.email && (
          <p id="email-error" className="text-sm text-red-600">
            {validationErrors.email}
          </p>
        )}

        {/* Password */}
        <label htmlFor="password" className="block text-sm font-medium">
          Password
        </label>
        <input
          id="password"
          type="password"
          placeholder="Password"
          value={password}
          disabled={submitting}
          onChange={(e) => {
            setPassword(e.target.value); // Fixed: was setEmail previously
            setValidationErrors((current) => ({
              ...current,
              password: "",
            }));
          }}
          aria-invalid={Boolean(validationErrors.password)}
          aria-describedby={
            validationErrors.password ? "password-error" : undefined
          }
          className="w-full rounded border p-3"
        />
        {validationErrors.password && (
          <p id="password-error" className="text-sm text-red-600">
            {validationErrors.password}
          </p>
        )}

        {loginError && <p className="text-red-600">{loginError}</p>}

        <button
          disabled={submitting}
          className="w-full rounded bg-blue-600 p-3 text-white hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? "Signing in..." : "Login"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm">
        Don't have an account?{" "}
        <Link
          to="/register"
          className="font-semibold text-blue-600 hover:underline"
        >
          Register
        </Link>
      </p>
    </main>
  );
}

export default LoginPage;
