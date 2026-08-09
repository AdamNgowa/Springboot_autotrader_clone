import { useState } from "react";
import { Navigate, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { validateRegister } from "../utils/validateAuth";

function RegisterPage() {
  const navigate = useNavigate();

  const { register, isAuthenticated, loading } = useAuth();
  const [validationErrors, setValidationErrors] = useState({});

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phoneNumber: "",
  });

  const [registerError, setRegisterError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (loading) {
    return <p className="p-6 text-center">Loading...</p>;
  }

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const errors = validateRegister(formData);

    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }

    setValidationErrors({});
    setRegisterError("");

    try {
      setSubmitting(true);

      await register(formData);

      navigate("/", { replace: true });
    } catch (error) {
      setRegisterError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="mx-auto mt-10 max-w-xl rounded-lg border bg-white p-8 shadow">
      <h1 className="mb-6 text-center text-3xl font-bold">Create Account</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* First name */}
        <label htmlFor="firstName" className="block text-sm font-medium">
          First name
        </label>
        <input
          id="firstName"
          name="firstName"
          placeholder="First name"
          value={formData.firstName}
          disabled={submitting}
          onChange={handleChange}
          aria-invalid={Boolean(validationErrors.firstName)}
          aria-describedby={
            validationErrors.firstName ? "firstName-error" : undefined
          }
          className="w-full rounded border p-3"
        />

        {validationErrors.firstName && (
          <p id="firstName-error" className="text-sm text-red-600">
            {validationErrors.firstName}
          </p>
        )}

        {/* Last name */}
        <label htmlFor="lastName" className="block text-sm font-medium">
          Last name
        </label>

        <input
          id="lastName"
          name="lastName"
          placeholder="Last name"
          value={formData.lastName}
          disabled={submitting}
          onChange={handleChange}
          aria-invalid={Boolean(validationErrors.lastName)}
          aria-describedby={
            validationErrors.lastName ? "lastName-error" : undefined
          }
          className="w-full rounded border p-3"
        />

        {validationErrors.lastName && (
          <p id="lastName-error" className="text-sm text-red-600">
            {validationErrors.lastName}
          </p>
        )}

        {/* Email */}
        <label htmlFor="email" className="block text-sm font-medium">
          Email
        </label>

        <input
          id="email"
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          disabled={submitting}
          onChange={handleChange}
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
          name="password"
          placeholder="Password"
          value={formData.password}
          disabled={submitting}
          onChange={handleChange}
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

        {/* Phone number */}

        <label htmlFor="phoneNumber" className="block text-sm font-medium">
          Phone number
        </label>

        <input
          id="phoneNumber"
          name="phoneNumber"
          placeholder="Phone number"
          value={formData.phoneNumber}
          disabled={submitting}
          onChange={handleChange}
          aria-invalid={Boolean(validationErrors.phoneNumber)}
          aria-describedby={
            validationErrors.phoneNumber ? "phoneNumber-error" : undefined
          }
          className="w-full rounded border p-3"
        />

        {validationErrors.phoneNumber && (
          <p id="phoneNumber-error" className="text-sm text-red-600">
            {validationErrors.phoneNumber}
          </p>
        )}
        {registerError && <p className="text-red-600">{registerError}</p>}

        <button
          disabled={submitting}
          className="w-full rounded bg-blue-600 p-3 text-white hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? "Creating account..." : "Register"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm">
        Already have an account?{" "}
        <Link
          to="/login"
          className="font-semibold text-blue-600 hover:underline"
        >
          Login
        </Link>
      </p>
    </main>
  );
}

export default RegisterPage;
