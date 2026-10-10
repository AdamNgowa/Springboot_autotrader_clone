import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import ChangePasswordForm from "../components/ChangePasswordForm";
import ProfileDetailsForm from "../components/ProfileDetailsForm";

function ProfilePage() {
    const { user } = useAuth();

    // ProtectedRoute guarantees a user, but guard anyway so a bad render never crashes.
    if (!user) {
        return null;
    }

    const fullName = `${user.firstName ?? ""} ${user.lastName ?? ""}`.trim();

    const memberSince = user.createdAt
        ? new Date(user.createdAt).toLocaleDateString(undefined, {
            year: "numeric",
            month: "long",
        })
        : null;

    return (
        <main className="mx-auto max-w-5xl px-4 py-6">
            <h1 className="mb-6 text-2xl font-bold">Profile</h1>

            {/* Summary */}
            <div className="mb-6 flex flex-wrap items-center gap-4 border border-slate-300 p-4">
        <span className="flex h-14 w-14 shrink-0 items-center justify-center bg-blue-600 text-2xl font-medium text-white">
          {user.firstName?.[0]?.toUpperCase() ?? "?"}
        </span>

                <div className="min-w-0 flex-1">
                    <p className="truncate text-lg font-semibold">{fullName}</p>
                    <p className="truncate text-sm text-slate-500">{user.email}</p>
                    {memberSince && (
                        <p className="text-xs text-slate-400">Member since {memberSince}</p>
                    )}
                </div>

                <Link to={`/sellers/${user.id}`} className="btn btn-outline">
                    View public profile
                </Link>
            </div>

            {/* items-start keeps each box its own height instead of stretching the shorter one. */}
            <div className="grid items-start gap-6 lg:grid-cols-2">
                <ProfileDetailsForm />
                <ChangePasswordForm />
            </div>
        </main>
    );
}

export default ProfilePage;