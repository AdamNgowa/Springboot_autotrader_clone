import { Link, useNavigate } from "react-router-dom";

import { useEffect, useState } from "react";

import { getImageUrl } from "../utils/getImageUrl";

import { useAuth } from "../hooks/useAuth";

import {
  addFavorite,
  getFavoriteStatus,
  removeFavorite,
} from "../api/favoriteApi";

function ListingCard({
  listing,
  onDelete,
  onUnfavorite,
  showOwnerActions = false,
}) {
  const navigate = useNavigate();

  const { isAuthenticated } = useAuth();

  const [isFavorite, setIsFavorite] = useState(false);

  const [favoriteLoading, setFavoriteLoading] = useState(false);

  const formattedPrice = new Intl.NumberFormat().format(listing.price);

  // Find the primary image, if one exists.
  const primaryImage = listing.images?.find((image) => image.primaryImage);

  /*
   * Load the current favorite state whenever:
   * - the listing changes
   * - authentication state becomes available
   */
  useEffect(() => {
    if (!isAuthenticated) {
      setIsFavorite(false);
      return;
    }

    async function loadFavoriteStatus() {
      try {
        const response = await getFavoriteStatus(listing.id);

        setIsFavorite(response?.favorite ?? false);
      } catch (error) {
        console.error("Failed to load favorite status:", error);
      }
    }

    loadFavoriteStatus();
  }, [listing.id, isAuthenticated]);

  async function handleFavoriteClick(event) {
    /*
     * Prevent the click from bubbling into the surrounding
     * listing Link.
     */
    event.preventDefault();
    event.stopPropagation();

    /*
     * Guests cannot use the favorites API.
     * Send them to login instead.
     */
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    /*
     * Prevent duplicate requests while an existing
     * favorite operation is still running.
     */
    if (favoriteLoading) {
      return;
    }

    try {
      setFavoriteLoading(true);

      if (isFavorite) {
        await removeFavorite(listing.id);
        setIsFavorite(false);
        // Lets a parent (the favorites page) remove the card from its list.
        onUnfavorite?.(listing.id);
      } else {
        await addFavorite(listing.id);
        setIsFavorite(true);
      }
    } catch (error) {
      console.error("Failed to update favorite:", error);
    } finally {
      setFavoriteLoading(false);
    }
  }

  return (
    // .lift / .lift-face (index.css) create the Twitch-style hover:
    // the face slides up and right and reveals a blue block behind it.
    <article className="lift">
      <div className="lift-face">
        <div className="relative">
          <Link to={`/listings/${listing.id}`} className="block">
            {/* Image: fixed aspect ratio so every card is the same height */}
            <div className="aspect-[4/3] w-full border-b border-slate-200 bg-slate-100">
              {primaryImage ? (
                <img
                  src={getImageUrl(primaryImage.imageUrl)}
                  alt={listing.title}
                  loading="lazy"
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="flex h-full items-center justify-center px-4 text-center">
                  <p className="text-sm font-medium text-slate-500">
                    No image available
                  </p>
                </div>
              )}
            </div>

            <div className="space-y-2 p-3">
              <p className="truncate text-base font-medium">
                {listing.year} {listing.make} {listing.model}
              </p>

              <div className="flex items-center gap-4 text-sm text-slate-500">
                <span className="flex min-w-0 items-center gap-1.5">
                  <LocationIcon />
                  <span className="truncate">{listing.city}</span>
                </span>

                <span className="flex shrink-0 items-center gap-1.5">
                  <MileageIcon />
                  {new Intl.NumberFormat().format(listing.mileage)} km
                </span>
              </div>

              <p className="text-lg font-bold">KSh {formattedPrice}</p>
            </div>
          </Link>

          {/* Favorite button */}
          <button
            type="button"
            onClick={handleFavoriteClick}
            disabled={favoriteLoading}
            aria-label={
              isFavorite ? "Remove from favorites" : "Add to favorites"
            }
            aria-pressed={isFavorite}
            className={`absolute right-2 top-2 flex h-9 w-9 items-center justify-center border border-slate-300 bg-white text-xl transition-colors hover:border-blue-600 hover:bg-blue-600 hover:text-white disabled:cursor-not-allowed disabled:opacity-60 ${
              isFavorite ? "text-red-600" : "text-slate-900"
            }`}
          >
            {isFavorite ? "♥" : "♡"}
          </button>
        </div>

        {showOwnerActions && (
          <footer className="flex gap-2 border-t border-slate-200 p-3">
            <button
              type="button"
              onClick={() => navigate(`/listings/${listing.id}/edit`)}
              className="btn btn-primary flex-1"
            >
              Edit
            </button>

            <button
              type="button"
              onClick={() => onDelete(listing.id)}
              className="btn btn-danger flex-1"
            >
              Delete
            </button>
          </footer>
        )}
      </div>
    </article>
  );
}

function LocationIcon() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      className="h-4 w-4 shrink-0"
      aria-hidden="true"
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
      />
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M19.5 10.5c0 5.25-7.5 10-7.5 10s-7.5-4.75-7.5-10a7.5 7.5 0 1 1 15 0Z"
      />
    </svg>
  );
}

function MileageIcon() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      className="h-4 w-4 shrink-0"
      aria-hidden="true"
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M12 3a9 9 0 1 0 9 9"
      />
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 7v5l3 2" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v2" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M21 12h-2" />
    </svg>
  );
}

export default ListingCard;
