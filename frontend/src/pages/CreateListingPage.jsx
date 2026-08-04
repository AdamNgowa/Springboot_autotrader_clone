import { useNavigate } from "react-router-dom";
import ListingForm from "../components/ListingForm";
import { useState } from "react";
import { createListing } from "../api/listingApi";

const EMPTY_LISTING = {
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

function CreateListingPage() {
  const navigate = useNavigate();

  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});

  function clearValidationError(fieldName) {
    setValidationErrors((currentErrors) => {
      const updatedErrors = { ...currentErrors };

      delete updatedErrors[fieldName];

      return updatedErrors;
    });
  }

  async function handleSubmit(newListing) {
    const clientValidationErrors = {};

    if (!newListing.fuelType) {
      clientValidationErrors.fuelType = "Fuel type is required";
    }

    if (!newListing.transmission) {
      clientValidationErrors.transmission = "Transmission is required";
    }

    if (!newListing.bodyType) {
      clientValidationErrors.bodyType = "Body type is required";
    }

    if (Object.keys(clientValidationErrors).length > 0) {
      setValidationErrors(clientValidationErrors);
      return;
    }

    try {
      setSaving(true);
      setError(null);
      setValidationErrors({});
      console.log("Submitting:", newListing);
      const createdListing = await createListing(newListing);
      console.log("Created listing:", createdListing);
      navigate("/my-listings");
    } catch (error) {
      console.log("Full error:", error);
      console.log("Error data:", error.data);
      if (error.data?.validationErrors) {
        const fieldErrors = {};

        error.data.validationErrors.forEach((item) => {
          fieldErrors[item.field] = item.message;
        });

        setValidationErrors(fieldErrors);
      } else {
        setError(error.message);
      }
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
        initialValues={EMPTY_LISTING}
        onSubmit={handleSubmit}
        saving={saving}
        submitText="Create listing"
        validationErrors={validationErrors}
        clearValidationError={clearValidationError}
      />
    </main>
  );
}

export default CreateListingPage;
