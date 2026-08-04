import { useNavigate } from "react-router-dom";
import ListingForm from "../components/ListingForm";
import { useState } from "react";
import { createListing } from "../api/listingApi";
import { validateListing } from "../utils/validateListing";

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

  // Function that takes the name of an input field (e.g., "email" or "username")
  function clearValidationError(fieldName) {
    // Call setValidationErrors using a callback function.
    // React automatically passes the freshest state object into 'currentErrors'.
    setValidationErrors((currentErrors) => {
      // 1. Create a shallow copy (clone) of the currentErrors object using the spread operator (...).
      // NEVER mutate React state directly; always modify a cloned copy instead!
      const updatedErrors = { ...currentErrors };

      // 2. Remove the specific error property matching fieldName from the cloned object.
      // e.g., if fieldName is "email", this removes updatedErrors.email
      delete updatedErrors[fieldName];

      // 3. Return the newly modified object. React sets this as the new state
      // and re-renders the UI, removing the error message from the screen.
      return updatedErrors;
    });
  }

  async function handleSubmit(newListing) {
    const clientValidationErrors = validateListing(newListing);
    //Object.keys(clientValidationErrors).length > 0: It takes the clientValidationErrors object, extracts an array of its keys,
    //  and checks if the count of keys is greater than 0 (meaning errors exist).
    if (Object.keys(clientValidationErrors).length > 0) {
      //If errors exist, it passes the full clientValidationErrors object to a React state updating function (setValidationErrors),
      // so the UI can render them.
      setValidationErrors(clientValidationErrors);
      //It stops the execution of the current function immediately and
      // returns undefined (an empty return used to exit early and prevent further code from running, such as making an API call).
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
