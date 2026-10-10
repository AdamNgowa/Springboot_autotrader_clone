import { useState } from "react";
import { changePassword } from "../api/userApi";
import { validatePasswordChange } from "../utils/validateAuth";
import FormField from "./FormField";
import Notice from "./Notice";

const EMPTY_FORM = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
};

function ChangePasswordForm() {
    const [formData, setFormData] = useState(EMPTY_FORM);
    const [validationErrors, setValidationErrors] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((current) => ({ ...current, [name]: value }));
        setValidationErrors((current) => ({ ...current, [name]: "" }));
        setSuccess("");
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const errors = validatePasswordChange(formData);

        if (Object.keys(errors).length > 0) {
            setValidationErrors(errors);
            return;
        }

        setValidationErrors({});
        setError("");
        setSuccess("");

        try {
            setSubmitting(true);

            // confirmPassword is a browser-only check and is never sent.
            await changePassword({
                currentPassword: formData.currentPassword,
                newPassword: formData.newPassword,
            });

            setFormData(EMPTY_FORM);
            setSuccess("Password changed.");
        } catch (error) {
            if (error.data?.validationErrors) {
                const fieldErrors = {};

                error.data.validationErrors.forEach((item) => {
                    fieldErrors[item.field] = item.message;
                });

                setValidationErrors(fieldErrors);
            } else {
                setError(error.message);
            }
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <section className="border border-slate-300 bg-white p-4 sm:p-6">
            <h2 className="text-lg font-semibold">Change password</h2>
            <p className="mb-4 mt-1 text-sm text-slate-500">
                You will need your current password. Use at least 8 characters.
            </p>

            <form onSubmit={handleSubmit} className="space-y-4">
                <FormField
                    label="Current password"
                    name="currentPassword"
                    type="password"
                    autoComplete="current-password"
                    value={formData.currentPassword}
                    disabled={submitting}
                    onChange={handleChange}
                    error={validationErrors.currentPassword}
                />

                <FormField
                    label="New password"
                    name="newPassword"
                    type="password"
                    autoComplete="new-password"
                    value={formData.newPassword}
                    disabled={submitting}
                    onChange={handleChange}
                    error={validationErrors.newPassword}
                />

                <FormField
                    label="Confirm new password"
                    name="confirmPassword"
                    type="password"
                    autoComplete="new-password"
                    value={formData.confirmPassword}
                    disabled={submitting}
                    onChange={handleChange}
                    error={validationErrors.confirmPassword}
                />

                {error && <Notice variant="error">{error}</Notice>}
                {success && <Notice variant="success">{success}</Notice>}

                <button
                    type="submit"
                    disabled={submitting}
                    className="btn btn-primary w-full sm:w-auto"
                >
                    {submitting ? "Changing..." : "Change password"}
                </button>
            </form>
        </section>
    );
}

export default ChangePasswordForm;