import { useState } from "react";
import { Navigate, Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const { login, isAuthenticated, loading } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loginError, setLoginError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (loading) {
    return <p className="p-6 text-center">Loading...</p>;
  }

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSubmitting(true);

      await login({
        email,
        password,
      });

      const redirectTo = location.state?.from?.pathname || "/";

      navigate(redirectTo, { replace: true });
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
        <input
          type="email"
          placeholder="Email"
          value={email}
          disabled={submitting}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full rounded border p-3"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          disabled={submitting}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full rounded border p-3"
        />

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
