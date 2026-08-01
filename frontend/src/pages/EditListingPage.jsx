import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getListing } from "../api/listingApi";

function EditListingPage() {
  const { id } = useParams();

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadListing() {
      try {
        const data = await getListing(id);
        setListing(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadListing();
  }, [id]);

  if (loading) {
    return <p>Loading listing...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  return (
    <main className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Edit Listing</h1>

      <pre>{JSON.stringify(listing, null, 2)}</pre>
    </main>
  );
}

export default EditListingPage;
