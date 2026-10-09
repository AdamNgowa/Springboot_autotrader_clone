import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { validateLogin } from "../utils/validateAuth";
import AuthCard from "../components/AuthCard";
import FormField from "../components/FormField";
import Notice from "../components/Notice";

function LoginPage() {
  const { login, isAuthenticated, loading } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loginError, setLoginError] = useState("");
  const [validationErrors, setValidationErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  if (loading) {
    return (
      <main className="flex min-h-[calc(100dvh-3.5rem)] items-center justify-center bg-slate-50">
        <p className="text-slate-600">Loading...</p>
      </main>
    );
  }

  if (isAuthenticated) {
    return null;
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
    <AuthCard
      title="Welcome back"
      subtitle="Sign in to your AutoTrader account"
      footer={
        <>
          Don't have an account?{" "}
          <Link
            to="/register"
            className="font-semibold text-blue-600 hover:underline"
          >
            Register
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <FormField
          label="Email"
          name="email"
          type="email"
          autoComplete="email"
          placeholder="Enter your email"
          value={email}
          disabled={submitting}
          onChange={(event) => {
            setEmail(event.target.value);

            setValidationErrors((current) => ({
              ...current,
              email: "",
            }));
          }}
          error={validationErrors.email}
        />

        <FormField
          label="Password"
          name="password"
          type="password"
          autoComplete="current-password"
          placeholder="Enter your password"
          value={password}
          disabled={submitting}
          onChange={(event) => {
            setPassword(event.target.value);

            setValidationErrors((current) => ({
              ...current,
              password: "",
            }));
          }}
          error={validationErrors.password}
        />

        {/* Server/API error */}
        {loginError && <Notice variant="error">{loginError}</Notice>}

        <button
          type="submit"
          disabled={submitting}
          className="btn btn-primary w-full"
        >
          {submitting ? "Signing in..." : "Login"}
        </button>
      </form>
    </AuthCard>
  );
}

export default LoginPage;
