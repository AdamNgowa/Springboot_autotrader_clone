import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getFavorites, removeFavorite } from "../api/favoriteApi";
import { getImageUrl } from "../utils/getImageUrl";

function FavoritesPage() {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadFavorites() {
      try {
        setLoading(true);
        setError("");

        const data = await getFavorites();

        setFavorites(data || []);
      } catch (err) {
        console.error("Failed to load favorites:", err);
        setError("Failed to load your favorites.");
      } finally {
        setLoading(false);
      }
    }

    loadFavorites();
  }, []);

  async function handleRemoveFavorite(listingId) {
    try {
      setError("");

      await removeFavorite(listingId);

      setFavorites((currentFavorites) =>
        currentFavorites.filter(
          (favorite) => favorite.listing.id !== listingId,
        ),
      );
    } catch (err) {
      console.error("Failed to remove favorite:", err);
      setError("Failed to remove the favorite.");
    }
  }

  if (loading) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <h1 className="text-3xl font-bold text-gray-900">My Favorites</h1>

        <p className="mt-4 text-gray-600">Loading favorites...</p>
      </main>
    );
  }

  if (error && favorites.length === 0) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <h1 className="text-3xl font-bold text-gray-900">My Favorites</h1>

        <div className="mt-6 rounded-lg border border-red-200 bg-red-50 p-4">
          <p className="text-red-700">{error}</p>
        </div>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-6xl p-6">
      <header className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">My Favorites</h1>

        <p className="mt-1 text-gray-600">
          {favorites.length}{" "}
          {favorites.length === 1 ? "favorite vehicle" : "favorite vehicles"}
        </p>
      </header>

      {error && (
        <div className="mb-6 rounded-lg border border-red-200 bg-red-50 p-4">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {favorites.length === 0 ? (
        <section className="rounded-lg border border-gray-200 bg-white p-8 text-center shadow-sm">
          <h2 className="text-xl font-semibold text-gray-900">
            No favorites yet
          </h2>

          <p className="mt-2 text-gray-600">
            You haven't added any vehicles to your favorites yet.
          </p>

          <Link
            to="/"
            className="mt-6 inline-block rounded-md bg-blue-600 px-5 py-2.5 font-medium text-white transition hover:bg-blue-700"
          >
            Browse Vehicles
          </Link>
        </section>
      ) : (
        <section className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
          {favorites.map((favorite) => {
            const listing = favorite.listing;

            const primaryImage =
              listing.images?.find((image) => image.primaryImage) ||
              listing.images?.[0];

            const formattedPrice = new Intl.NumberFormat().format(
              listing.price,
            );

            return (
              <article
                key={favorite.id}
                className="overflow-hidden rounded-lg border border-transparent bg-white shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-slate-300 hover:shadow-lg"
              >
                <div className="flex h-48 items-center justify-center bg-gray-200">
                  {primaryImage ? (
                    <img
                      src={getImageUrl(primaryImage.imageUrl)}
                      alt={listing.title}
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <p className="px-4 text-center text-sm font-medium text-gray-600">
                      No image available
                    </p>
                  )}
                </div>

                <div className="flex flex-col gap-4 p-4">
                  <section className="flex flex-col gap-1">
                    <h2 className="text-lg font-medium text-gray-900">
                      {listing.title}
                    </h2>

                    <p className="text-sm text-gray-600">
                      {listing.year} {listing.make} {listing.model}
                    </p>

                    <p className="text-sm text-gray-500">{listing.city}</p>
                  </section>

                  <section className="space-y-1">
                    <p className="text-2xl font-bold text-gray-900">
                      KSh {formattedPrice}
                    </p>

                    <p className="text-sm text-gray-500">
                      {listing.mileage?.toLocaleString()} km
                    </p>
                  </section>

                  <div className="flex gap-3 border-t pt-4">
                    <Link
                      to={`/listings/${listing.id}`}
                      className="flex-1 rounded-md bg-blue-600 px-4 py-2 text-center font-medium text-white transition hover:bg-blue-700"
                    >
                      View Listing
                    </Link>

                    <button
                      type="button"
                      onClick={() => handleRemoveFavorite(listing.id)}
                      className="rounded-md border border-red-600 px-4 py-2 font-medium text-red-600 transition hover:bg-red-50"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </article>
            );
          })}
        </section>
      )}
    </main>
  );
}

export default FavoritesPage;
