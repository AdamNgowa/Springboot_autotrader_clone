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
      // Call setListings using the "functional update pattern" (passing a callback function).
      // React automatically runs this callback and passes in the freshest state array
      // as the first argument, which we name 'currentListings'.
      setListings((currentListings) =>
        // .filter() creates a BRAND NEW array by looping through every item in 'currentListings'.
        // For each individual 'listing' in the array, it evaluates the condition:
        // Is this listing's ID NOT EQUAL to the ID we just deleted?
        //
        // - If TRUE (IDs don't match): Keep this listing in the new array.
        // - If FALSE (IDs match): Drop this listing from the new array.
        currentListings.filter((listing) => listing.id !== id),
      );
      // React receives the new filtered array, replaces the old state, and re-renders the UI
      // so the deleted item instantly vanishes from the screen.
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
