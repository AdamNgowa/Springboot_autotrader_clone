import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { updateListing, getListing } from "../api/listingApi";
import ListingForm from "../components/ListingForm";
import { validateListing } from "../utils/validateListing";

function EditListingPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const SUCCESS_MESSAGE_DURATION = 3000;

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState("");
  const [validationErrors, setValidationErrors] = useState({});

  function clearValidationError(fieldName) {
    setValidationErrors((current) => {
      const updated = { ...current };
      delete updated[fieldName];
      return updated;
    });
  }

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
    const clientValidationErrors = validateListing(updatedListing);
    if (Object.keys(clientValidationErrors).length > 0) {
      setValidationErrors(clientValidationErrors);
      return;
    }
    try {
      setSuccess("");
      setSaving(true);

      await updateListing(id, updatedListing);

      setSuccess("Listing updated successfully.");

      // setTimeout(functionToRunLater, milliseconds);
      setTimeout(() => {
        setSuccess("");
        navigate("/my-listings");
      }, SUCCESS_MESSAGE_DURATION);
    } catch (error) {
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

  if (loading) {
    return <p>Loading listing...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  return (
    <main className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Edit Listing</h1>
      {success && (
        <div
          className="mb-4 flex items-center justify-between rounded-md
               bg-green-100 p-3 text-green-800"
        >
          <span>{success}</span>
          <button
            type="button"
            onClick={() => setSuccess("")}
            className="font-bold hover:text-green-950"
          >
            X
          </button>
        </div>
      )}
      <ListingForm
        initialValues={listing}
        onSubmit={handleSubmit}
        submitText="Update listing"
        saving={saving}
        validationErrors={validationErrors}
        clearValidationError={clearValidationError}
      />
    </main>
  );
}

export default EditListingPage;
