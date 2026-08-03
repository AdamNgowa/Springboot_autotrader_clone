import { useNavigate } from "react-router-dom";
import ListingForm from "../components/ListingForm";
import { useState } from "react";
import { createListing } from "../api/listingApi";
import {
  FUEL_TYPES,
  TRANSMISSIONS,
  BODY_TYPES,
} from "../constants/listingEnums";

function CreateListingPage() {
  const navigate = useNavigate();

  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const initialValues = {
    title: "",
    description: "",
    price: "",
    year: "",
    make: "",
    model: "",
    mileage: "",
    city: "",
    fuelType: "",
    transmission: "",
    bodyType: "",
  };

  async function handleSubmit(newListing) {
    try {
      setSaving(true);
      setError(null);
      console.log("Submitting:", newListing);
      const createdListing = await createListing(newListing);
      console.log("Created listing:", createdListing);
      navigate("/my-listings");
    } catch (error) {
      setError("Failed to create listing.");
      console.error("Status:", error.status);
      console.error("Message:", error.message);
      console.error("Response:", error.data);
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Create Listing</h1>
      {error && (
        <div className="mb-4 rounded bg-red-100 p-3 text-red-700">{error}</div>
      )}
      <ListingForm
        initialValues={initialValues}
        onSubmit={handleSubmit}
        saving={saving}
        submitText="Create listing"
      />
    </main>
  );
}

export default CreateListingPage;
