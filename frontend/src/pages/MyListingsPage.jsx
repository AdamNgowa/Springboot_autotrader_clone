import { useEffect, useState } from "react";
import { deleteListing, getMyListings } from "../api/listingApi";
import ListingCard from "../components/ListingCard";

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
      await deleteListing(id);
      setListings((currentListings) =>
        currentListings.filter((listing) => listing.id !== id),
      );
    } catch (error) {
      setError(error.message);
    }
  }

  if (loading) {
    return <p>Loading listings...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  return (
    <main className="max-w-6xl mx-auto p-6">
      <h1>My Listings</h1>
      <p>Total listings: {listings.length}</p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 m-2">
        {listings.map((listing) => (
          <ListingCard
            key={listing.id}
            listing={listing}
            onDelete={handleDelete}
          />
        ))}
      </div>
    </main>
  );
}

export default MyListingsPage;
