import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { updateListing, getListing } from "../api/listingApi";
import Lf from "../components/ListingForm";

function EditListingPage() {
  const { id } = useParams();

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  //Whenever the id changes , run the effect (runs getListing api call)
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

  async function handleSubmit(updatedListing) {
    try {
      setSaving(true);
      const updated = await updateListing(id, updatedListing);
      console.log("Updated listing:", updated);
    } catch (error) {
      console.error(error);
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return <p>Loading listing...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  return (
    <main className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Edit Listing</h1>
      <Lf
        initialValues={listing}
        onSubmit={handleSubmit}
        submitText="Update listing"
        saving={saving}
      />
    </main>
  );
}

export default EditListingPage;
