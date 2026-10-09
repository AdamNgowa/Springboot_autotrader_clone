import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { validateRegister } from "../utils/validateAuth";
import AuthCard from "../components/AuthCard";
import FormField from "../components/FormField";
import Notice from "../components/Notice";

// Field definitions: the form below simply maps over this list.
const FIELDS = [
  {
    name: "firstName",
    label: "First name",
    placeholder: "Enter your first name",
    autoComplete: "given-name",
  },
  {
    name: "lastName",
    label: "Last name",
    placeholder: "Enter your last name",
    autoComplete: "family-name",
  },
  {
    name: "email",
    label: "Email",
    type: "email",
    placeholder: "Enter your email",
    autoComplete: "email",
    full: true,
  },
  {
    name: "password",
    label: "Password",
    type: "password",
    placeholder: "Create a password",
    autoComplete: "new-password",
    full: true,
  },
  {
    name: "phoneNumber",
    label: "Phone number",
    type: "tel",
    placeholder: "Enter your phone number",
    autoComplete: "tel",
    full: true,
  },
];

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
      <main className="flex min-h-[calc(100dvh-3.5rem)] items-center justify-center bg-slate-50">
        <p className="text-slate-600">Loading...</p>
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
    <AuthCard
      title="Create your account"
      subtitle="Join AutoTrader and start buying or selling vehicles."
      footer={
        <>
          Already have an account?{" "}
          <Link
            to="/login"
            className="font-semibold text-blue-600 hover:underline"
          >
            Login
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2">
        {FIELDS.map((field) => (
          <FormField
            key={field.name}
            label={field.label}
            name={field.name}
            type={field.type}
            autoComplete={field.autoComplete}
            placeholder={field.placeholder}
            value={formData[field.name]}
            disabled={submitting}
            onChange={handleChange}
            error={validationErrors[field.name]}
            className={field.full ? "sm:col-span-2" : ""}
          />
        ))}

        {/* Server/API error */}
        {registerError && (
          <Notice variant="error" className="sm:col-span-2">
            {registerError}
          </Notice>
        )}

        <button
          type="submit"
          disabled={submitting}
          className="btn btn-primary w-full sm:col-span-2"
        >
          {submitting ? "Creating account..." : "Register"}
        </button>
      </form>
    </AuthCard>
  );
}

export default RegisterPage;
