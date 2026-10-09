import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { useAuth } from "../hooks/useAuth";
import { getMyListings } from "../api/listingApi";
import { getFavorites } from "../api/favoriteApi";
import { getMyConversations } from "../api/messagingApi";
import ListingCard from "../components/ListingCard";
import ListingGrid from "../components/ListingGrid";
import Notice from "../components/Notice";

// One clickable figure. The .lift classes give it the same hover as the cards.
function StatTile({ to, label, value }) {
  return (
    <Link to={to} className="lift block">
      <div className="lift-face p-4">
        <p className="text-xs font-medium text-slate-500">{label}</p>
        <p className="mt-1 text-3xl font-bold">{value ?? "–"}</p>
      </div>
    </Link>
  );
}

function DashboardPage() {
  const { user } = useAuth();

  const [stats, setStats] = useState({
    listings: null,
    favorites: null,
    conversations: null,
  });
  const [recentListings, setRecentListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadDashboard() {
      // allSettled: one failing request must not take the whole page down.
      const [listingsResult, favoritesResult, conversationsResult] =
        await Promise.allSettled([
          getMyListings(),
          getFavorites(),
          getMyConversations(0, 1),
        ]);

      const listingsPage =
        listingsResult.status === "fulfilled" ? listingsResult.value : null;

      setRecentListings(listingsPage?.content?.slice(0, 3) ?? []);

      setStats({
        listings: listingsPage
          ? (listingsPage.totalElements ?? listingsPage.content?.length ?? 0)
          : null,
        favorites:
          favoritesResult.status === "fulfilled"
            ? (favoritesResult.value?.length ?? 0)
            : null,
        conversations:
          conversationsResult.status === "fulfilled"
            ? (conversationsResult.value?.totalElements ?? 0)
            : null,
      });

      const anyFailed = [
        listingsResult,
        favoritesResult,
        conversationsResult,
      ].some((result) => result.status === "rejected");

      setError(anyFailed ? "Some figures could not be loaded." : null);
      setLoading(false);
    }

    loadDashboard();
  }, []);

  // While loading, show an ellipsis instead of a misleading zero.
  const show = (value) => (loading ? "…" : value);

  return (
    <main className="mx-auto max-w-6xl px-4 py-6">
      <header className="mb-6 flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">
            Welcome back{user?.firstName ? `, ${user.firstName}` : ""}
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Here is a quick look at your account.
          </p>
        </div>

        <div className="flex gap-2">
          <Link to="/listings/new" className="btn btn-primary">
            Sell vehicle
          </Link>
          <Link to="/" className="btn btn-outline">
            Browse
          </Link>
        </div>
      </header>

      {error && (
        <Notice variant="warning" className="mb-4">
          {error}
        </Notice>
      )}

      {/* The grid gap (24px) leaves room for the tiles' hover offset. */}
      <div className="grid gap-6 sm:grid-cols-3">
        <StatTile
          to="/my-listings"
          label="My listings"
          value={show(stats.listings)}
        />
        <StatTile
          to="/favorites"
          label="Favorites"
          value={show(stats.favorites)}
        />
        <StatTile
          to="/conversations"
          label="Conversations"
          value={show(stats.conversations)}
        />
      </div>

      <section className="mt-10">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-bold">Your latest listings</h2>

          <Link
            to="/my-listings"
            className="text-sm font-medium text-blue-600 hover:underline"
          >
            View all
          </Link>
        </div>

        {!loading && recentListings.length === 0 ? (
          <div className="border border-slate-300 p-8 text-center">
            <p className="font-medium">You have no listings yet</p>
            <p className="mt-1 text-sm text-slate-500">
              Post your first vehicle and it will show up here.
            </p>
            <Link to="/listings/new" className="btn btn-primary mt-4">
              Create listing
            </Link>
          </div>
        ) : (
          <ListingGrid>
            {recentListings.map((listing) => (
              <ListingCard key={listing.id} listing={listing} />
            ))}
          </ListingGrid>
        )}
      </section>
    </main>
  );
}

export default DashboardPage;
