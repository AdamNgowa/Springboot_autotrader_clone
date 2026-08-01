import { useEffect, useState } from "react";
import { getMyListings } from "../api/listingApi";
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
          <ListingCard key={listing.id} listing={listing} />
        ))}
      </div>
    </main>
  );
}

export default MyListingsPage;
