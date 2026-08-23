import { Link, useNavigate } from "react-router-dom";

import { useEffect, useState } from "react";

import { getImageUrl } from "../utils/getImageUrl";

import { useAuth } from "../hooks/useAuth";

import {
  addFavorite,
  getFavoriteStatus,
  removeFavorite,
} from "../api/favoriteApi";

function ListingCard({ listing, onDelete, showOwnerActions = false }) {
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
    <article className="overflow-hidden rounded-lg border border-transparent shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-slate-300 hover:shadow-lg">
      <div className="relative">
        <Link
          to={`/listings/${listing.id}`}
          className="block focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
        >
          <div className="flex h-48 items-center justify-center bg-gray-200">
            {primaryImage ? (
              <img
                src={getImageUrl(primaryImage.imageUrl)}
                alt={listing.title}
                className="h-full w-full object-cover"
              />
            ) : (
              <div className="px-4 text-center">
                <p className="text-sm font-medium text-gray-600">
                  No image available
                </p>
              </div>
            )}
          </div>

          <div className="flex flex-col gap-4 p-4">
            {/* Vehicle title */}
            <section>
              <p className="text-lg font-medium">
                {listing.year} {listing.make} {listing.model}
              </p>
            </section>

            {/* Location + Mileage */}
            <section className="flex gap-3 items-center  border-t border-gray-100 pt-3">
              {/* Location */}
              <div className="flex items-center gap-1.5 text-sm text-gray-500">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                  className="h-4 w-4"
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

                <span>{listing.city}</span>
              </div>

              {/* Mileage */}
              <div className="flex items-center gap-1.5 text-sm text-gray-500">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                  className="h-4 w-4"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M12 3a9 9 0 1 0 9 9"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M12 7v5l3 2"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M12 3v2"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M21 12h-2"
                  />
                </svg>

                <span>
                  {new Intl.NumberFormat().format(listing.mileage)} km
                </span>
              </div>
            </section>

            {/* Price */}
            <section className="mt-auto">
              <strong className="text-2xl font-bold">
                KSh {formattedPrice}
              </strong>
            </section>
          </div>
        </Link>

        {/* Favorite button */}
        <button
          type="button"
          onClick={handleFavoriteClick}
          disabled={favoriteLoading}
          aria-label={isFavorite ? "Remove from favorites" : "Add to favorites"}
          aria-pressed={isFavorite}
          className="absolute right-3 top-3 flex h-10 w-10 items-center justify-center rounded-full bg-white text-2xl shadow-md transition hover:scale-105 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isFavorite ? "♥" : "♡"}
        </button>
      </div>

      {showOwnerActions && (
        <footer className="border-t p-4">
          <div className="flex justify-between">
            <button
              type="button"
              onClick={() => navigate(`/listings/${listing.id}/edit`)}
              className="rounded-md bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
            >
              Edit
            </button>

            <button
              type="button"
              onClick={() => onDelete(listing.id)}
              className="rounded-md bg-red-600 px-4 py-2 text-white transition-colors hover:bg-red-700"
            >
              Delete
            </button>
          </div>
        </footer>
      )}
    </article>
  );
}

export default ListingCard;
