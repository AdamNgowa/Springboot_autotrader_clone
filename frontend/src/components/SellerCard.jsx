import { Link } from "react-router-dom";

function SellerCard({ seller, isOwner, onMessage, messaging, error }) {
  if (!seller) {
    return (
      <div className="border border-slate-300 p-4 text-sm text-slate-500">
        Seller information is not available.
      </div>
    );
  }

  const initial = seller.firstName?.[0]?.toUpperCase() ?? "?";

  return (
    <div className="border border-slate-300 p-4">
      <p className="mb-3 text-xs font-medium text-slate-500">Seller</p>

      <div className="flex items-center gap-3">
        <span className="flex h-10 w-10 shrink-0 items-center justify-center bg-blue-600 font-medium text-white">
          {initial}
        </span>

        <div className="min-w-0">
          <Link
            to={`/sellers/${seller.id}`}
            className="block truncate font-semibold text-blue-600 hover:underline"
          >
            {seller.firstName} {seller.lastName}
          </Link>

          <p className="truncate text-sm text-slate-500">
            {seller.phoneNumber || "No phone number provided"}
          </p>
        </div>
      </div>

      {!isOwner && (
        <div className="mt-4 flex flex-col gap-2">
          {seller.phoneNumber && (
            <a href={`tel:${seller.phoneNumber}`} className="btn btn-primary">
              Contact seller
            </a>
          )}

          <button
            type="button"
            onClick={onMessage}
            disabled={messaging}
            className="btn btn-outline hidden lg:inline-flex"
          >
            {messaging ? "Starting conversation..." : "Message seller"}
          </button>
        </div>
      )}

      {error && (
        <p className="mt-3 text-sm text-red-600">
          Unable to start conversation: {error}
        </p>
      )}
    </div>
  );
}

export default SellerCard;
