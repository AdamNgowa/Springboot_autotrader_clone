import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getFavorites } from "../api/favoriteApi";
import ListingCard from "../components/ListingCard";
import ListingGrid from "../components/ListingGrid";
import ListingGridSkeleton from "../components/ListingGridSkeleton";
import Notice from "../components/Notice";

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

  // ListingCard performs the API call; here we only drop the card from the page.
  function handleUnfavorite(listingId) {
    setFavorites((current) =>
      current.filter((favorite) => favorite.listing.id !== listingId),
    );
  }

  return (
    <main className="mx-auto max-w-7xl px-4 py-6">
      <header className="mb-6">
        <h1 className="text-2xl font-bold">Favorites</h1>

        {!loading && !error && (
          <p className="mt-1 text-sm text-slate-500">
            {favorites.length}{" "}
            {favorites.length === 1 ? "saved vehicle" : "saved vehicles"}
          </p>
        )}
      </header>

      {error && <Notice variant="error">{error}</Notice>}

      {loading ? (
        <ListingGridSkeleton count={3} />
      ) : (
        !error &&
        (favorites.length === 0 ? (
          <div className="border border-slate-300 p-8 text-center">
            <p className="font-medium">No favorites yet</p>
            <p className="mt-1 text-sm text-slate-500">
              Tap the heart on any vehicle to save it here.
            </p>
            <Link to="/" className="btn btn-primary mt-4">
              Browse vehicles
            </Link>
          </div>
        ) : (
          <ListingGrid>
            {favorites.map((favorite) => (
              <ListingCard
                key={favorite.id}
                listing={favorite.listing}
                onUnfavorite={handleUnfavorite}
              />
            ))}
          </ListingGrid>
        ))
      )}
    </main>
  );
}

export default FavoritesPage;
