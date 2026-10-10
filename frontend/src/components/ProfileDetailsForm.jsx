import { useState } from "react";
import { updateCurrentUser } from "../api/userApi";
import { useAuth } from "../hooks/useAuth";
import { validateProfile } from "../utils/validateAuth";
import FormField from "./FormField";
import Notice from "./Notice";

function ProfileDetailsForm() {
    const { user, updateUser } = useAuth();

    const [formData, setFormData] = useState({
        firstName: user.firstName ?? "",
        lastName: user.lastName ?? "",
        phoneNumber: user.phoneNumber ?? "",
    });
    const [validationErrors, setValidationErrors] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    // Nothing to save until a value differs from what is stored (spaces ignored).
    const unchanged =
        formData.firstName.trim() === (user.firstName ?? "") &&
        formData.lastName.trim() === (user.lastName ?? "") &&
        formData.phoneNumber.trim() === (user.phoneNumber ?? "");

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((current) => ({ ...current, [name]: value }));
        setValidationErrors((current) => ({ ...current, [name]: "" }));
        setSuccess("");
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const errors = validateProfile(formData);

        if (Object.keys(errors).length > 0) {
            setValidationErrors(errors);
            return;
        }

        setValidationErrors({});
        setError("");
        setSuccess("");

        try {
            setSubmitting(true);

            const updated = await updateCurrentUser(formData);

            // Share the new profile with the rest of the app, and show the saved values.
            updateUser(updated);
            setFormData({
                firstName: updated.firstName ?? "",
                lastName: updated.lastName ?? "",
                phoneNumber: updated.phoneNumber ?? "",
            });
            setSuccess("Profile updated.");
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
            <h2 className="text-lg font-semibold">Personal details</h2>
            <p className="mb-4 mt-1 text-sm text-slate-500">
                Buyers see your name and phone number on your listings.
            </p>

            <form onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2">
                <FormField
                    label="First name"
                    name="firstName"
                    autoComplete="given-name"
                    value={formData.firstName}
                    disabled={submitting}
                    onChange={handleChange}
                    error={validationErrors.firstName}
                />

                <FormField
                    label="Last name"
                    name="lastName"
                    autoComplete="family-name"
                    value={formData.lastName}
                    disabled={submitting}
                    onChange={handleChange}
                    error={validationErrors.lastName}
                />

                <FormField
                    label="Phone number"
                    name="phoneNumber"
                    type="tel"
                    autoComplete="tel"
                    value={formData.phoneNumber}
                    disabled={submitting}
                    onChange={handleChange}
                    error={validationErrors.phoneNumber}
                    className="sm:col-span-2"
                />

                <FormField
                    label="Email"
                    name="email"
                    value={user.email}
                    disabled
                    hint="Your email is your login and can't be changed here."
                    className="sm:col-span-2"
                />

                {error && (
                    <Notice variant="error" className="sm:col-span-2">
                        {error}
                    </Notice>
                )}

                {success && (
                    <Notice variant="success" className="sm:col-span-2">
                        {success}
                    </Notice>
                )}

                <div className="sm:col-span-2">
                    <button
                        type="submit"
                        disabled={submitting || unchanged}
                        className="btn btn-primary w-full sm:w-auto"
                    >
                        {submitting ? "Saving..." : "Save changes"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default ProfileDetailsForm;