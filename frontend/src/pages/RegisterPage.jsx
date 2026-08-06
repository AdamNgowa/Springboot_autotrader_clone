import { useState } from "react";
import { Navigate, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function RegisterPage() {
  const navigate = useNavigate();

  const { register, isAuthenticated, loading } = useAuth();

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
        <input
          name="firstName"
          placeholder="First name"
          value={formData.firstName}
          disabled={submitting}
          onChange={handleChange}
          className="w-full rounded border p-3"
        />

        <input
          name="lastName"
          placeholder="Last name"
          value={formData.lastName}
          disabled={submitting}
          onChange={handleChange}
          className="w-full rounded border p-3"
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          disabled={submitting}
          onChange={handleChange}
          className="w-full rounded border p-3"
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          disabled={submitting}
          onChange={handleChange}
          className="w-full rounded border p-3"
        />

        <input
          name="phoneNumber"
          placeholder="Phone number"
          value={formData.phoneNumber}
          disabled={submitting}
          onChange={handleChange}
          className="w-full rounded border p-3"
        />

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
