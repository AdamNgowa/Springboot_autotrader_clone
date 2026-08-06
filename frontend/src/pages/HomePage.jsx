import { useEffect, useState } from "react";
import { getListings } from "../api/listingApi";
import ListingCard from "../components/ListingCard";

function HomePage() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadListings() {
      try {
        const data = await getListings();
        setListings(data.content);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadListings();
  }, []);

  if (loading) {
    return (
      <main className="max-w-6xl mx-auto p-6">
        <p>Loading listings...</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="max-w-6xl mx-auto p-6">
        <p>Error: {error}</p>
      </main>
    );
  }

  if (listings.length === 0) {
    return (
      <main className="max-w-6xl mx-auto p-6">
        <p>No listings available.</p>
      </main>
    );
  }

  return (
    <main className="max-w-6xl mx-auto p-6">
      <h1 className="mb-6 text-3xl font-bold">Latest Vehicles</h1>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        {listings.map((listing) => (
          <ListingCard key={listing.id} listing={listing} />
        ))}
      </div>
    </main>
  );
}

export default HomePage;
