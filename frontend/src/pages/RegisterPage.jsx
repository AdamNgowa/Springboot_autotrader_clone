import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
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
    return (
      <main className="flex min-h-screen items-center justify-center bg-gray-50">
        <p className="text-gray-600">Loading...</p>
      </main>
    );
  }

  if (isAuthenticated) {
    return null;
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));

    // Clear the error for this specific field as the user types.
    setValidationErrors((current) => ({
      ...current,
      [name]: "",
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
    <main className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12">
      <section className="w-full max-w-md rounded-xl bg-white p-8 shadow-md">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-gray-900">
            Create your account
          </h1>

          <p className="mt-2 text-sm text-gray-600">
            Join AutoTrader and start buying or selling vehicles.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* First name */}
          <div>
            <label
              htmlFor="firstName"
              className="mb-2 block text-sm font-medium text-gray-700"
            >
              First name
            </label>

            <input
              id="firstName"
              name="firstName"
              placeholder="Enter your first name"
              value={formData.firstName}
              disabled={submitting}
              onChange={handleChange}
              aria-invalid={Boolean(validationErrors.firstName)}
              aria-describedby={
                validationErrors.firstName ? "firstName-error" : undefined
              }
              className="w-full rounded-md border border-gray-300 p-3 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-100"
            />

            {validationErrors.firstName && (
              <p id="firstName-error" className="mt-1 text-sm text-red-600">
                {validationErrors.firstName}
              </p>
            )}
          </div>

          {/* Last name */}
          <div>
            <label
              htmlFor="lastName"
              className="mb-2 block text-sm font-medium text-gray-700"
            >
              Last name
            </label>

            <input
              id="lastName"
              name="lastName"
              placeholder="Enter your last name"
              value={formData.lastName}
              disabled={submitting}
              onChange={handleChange}
              aria-invalid={Boolean(validationErrors.lastName)}
              aria-describedby={
                validationErrors.lastName ? "lastName-error" : undefined
              }
              className="w-full rounded-md border border-gray-300 p-3 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-100"
            />

            {validationErrors.lastName && (
              <p id="lastName-error" className="mt-1 text-sm text-red-600">
                {validationErrors.lastName}
              </p>
            )}
          </div>

          {/* Email */}
          <div>
            <label
              htmlFor="email"
              className="mb-2 block text-sm font-medium text-gray-700"
            >
              Email
            </label>

            <input
              id="email"
              type="email"
              name="email"
              placeholder="Enter your email"
              value={formData.email}
              disabled={submitting}
              onChange={handleChange}
              aria-invalid={Boolean(validationErrors.email)}
              aria-describedby={
                validationErrors.email ? "email-error" : undefined
              }
              className="w-full rounded-md border border-gray-300 p-3 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-100"
            />

            {validationErrors.email && (
              <p id="email-error" className="mt-1 text-sm text-red-600">
                {validationErrors.email}
              </p>
            )}
          </div>

          {/* Password */}
          <div>
            <label
              htmlFor="password"
              className="mb-2 block text-sm font-medium text-gray-700"
            >
              Password
            </label>

            <input
              id="password"
              type="password"
              name="password"
              placeholder="Create a password"
              value={formData.password}
              disabled={submitting}
              onChange={handleChange}
              aria-invalid={Boolean(validationErrors.password)}
              aria-describedby={
                validationErrors.password ? "password-error" : undefined
              }
              className="w-full rounded-md border border-gray-300 p-3 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-100"
            />

            {validationErrors.password && (
              <p id="password-error" className="mt-1 text-sm text-red-600">
                {validationErrors.password}
              </p>
            )}
          </div>

          {/* Phone number */}
          <div>
            <label
              htmlFor="phoneNumber"
              className="mb-2 block text-sm font-medium text-gray-700"
            >
              Phone number
            </label>

            <input
              id="phoneNumber"
              name="phoneNumber"
              placeholder="Enter your phone number"
              value={formData.phoneNumber}
              disabled={submitting}
              onChange={handleChange}
              aria-invalid={Boolean(validationErrors.phoneNumber)}
              aria-describedby={
                validationErrors.phoneNumber ? "phoneNumber-error" : undefined
              }
              className="w-full rounded-md border border-gray-300 p-3 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-100"
            />

            {validationErrors.phoneNumber && (
              <p id="phoneNumber-error" className="mt-1 text-sm text-red-600">
                {validationErrors.phoneNumber}
              </p>
            )}
          </div>

          {/* Server/API error */}
          {registerError && (
            <p
              role="alert"
              className="rounded-md bg-red-50 p-3 text-sm text-red-600"
            >
              {registerError}
            </p>
          )}

          <button
            type="submit"
            disabled={submitting}
            className="w-full rounded-md bg-blue-600 p-3 font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {submitting ? "Creating account..." : "Register"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-gray-600">
          Already have an account?{" "}
          <Link
            to="/login"
            className="font-semibold text-blue-600 hover:underline"
          >
            Login
          </Link>
        </p>
      </section>
    </main>
  );
}

export default RegisterPage;
