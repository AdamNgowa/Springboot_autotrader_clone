import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteListing, getMyListings } from "../api/listingApi";
import ListingCard from "../components/ListingCard";
import ListingGrid from "../components/ListingGrid";
import ListingGridSkeleton from "../components/ListingGridSkeleton";
import Notice from "../components/Notice";

function MyListingsPage() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadListings() {
      try {
        const data = await getMyListings();
        setListings(data.content);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }
    loadListings();
  }, []);

  async function handleDelete(id) {
    const confirmed = window.confirm(
      "Are you sure you want to delete the listing?",
    );

    if (!confirmed) {
      return;
    }
    try {
      setError(null);
      await deleteListing(id);
      // Functional update: build a new array without the deleted listing,
      // so React re-renders and the card disappears.
      setListings((currentListings) =>
        currentListings.filter((listing) => listing.id !== id),
      );
    } catch (error) {
      setError(error.message);
    }
  }

  return (
    <main className="mx-auto max-w-7xl px-4 py-6">
      <div className="mb-6 flex items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">My listings</h1>

          {!loading && (
            <p className="mt-1 text-sm text-slate-500">
              {listings.length} listing{listings.length !== 1 && "s"}
            </p>
          )}
        </div>

        <Link to="/listings/new" className="btn btn-primary">
          Sell vehicle
        </Link>
      </div>

      {error && (
        <Notice variant="error" className="mb-4">
          {error}
        </Notice>
      )}

      {loading ? (
        <ListingGridSkeleton count={3} />
      ) : listings.length === 0 ? (
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
          {listings.map((listing) => (
            <ListingCard
              key={listing.id}
              listing={listing}
              onDelete={handleDelete}
              showOwnerActions
            />
          ))}
        </ListingGrid>
      )}
    </main>
  );
}

export default MyListingsPage;
