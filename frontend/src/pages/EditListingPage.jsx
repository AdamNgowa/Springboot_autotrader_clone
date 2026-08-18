import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { updateListing, getListing } from "../api/listingApi";
import ListingForm from "../components/ListingForm";
import ImageManager from "../components/ImageManager";
import { validateListing } from "../utils/validateListing";

function EditListingPage() {
  const { id } = useParams();
  const navigate = useNavigate();

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
      setError(null);
      setSaving(true);

      const updated = await updateListing(id, updatedListing);

      setListing((current) => ({
        ...current,
        ...updated,
      }));

      setSuccess("Listing updated successfully.");
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

  async function refreshListing(deletedImageId) {
    try {
      const data = await getListing(id);

      if (deletedImageId) {
        data.images = (data.images || []).filter(
          (image) => String(image.id) !== String(deletedImageId),
        );
      }

      setListing(data);
    } catch (error) {
      setError(error.message);
    }
  }

  if (loading) {
    return <p>Loading listing...</p>;
  }

  if (error && !listing) {
    return <p>Error: {error}</p>;
  }

  return (
    <main className="mx-auto max-w-4xl p-6">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-3xl font-bold">Edit Listing</h1>

        <button
          type="button"
          onClick={() => navigate(`/listings/${id}`)}
          className="rounded-lg border border-slate-300 px-4 py-2 text-sm hover:bg-slate-100"
        >
          View Listing
        </button>
      </div>

      {error && (
        <div className="mb-4 rounded bg-red-100 p-3 text-red-700">{error}</div>
      )}

      {success && (
        <div className="mb-4 rounded-md bg-green-100 p-3 text-green-800">
          {success}
        </div>
      )}

      <div className="space-y-8">
        <ListingForm
          initialValues={listing}
          onSubmit={handleSubmit}
          submitText="Update listing"
          saving={saving}
          validationErrors={validationErrors}
          clearValidationError={clearValidationError}
          showImageUpload={false}
        />

        <ImageManager
          listingId={listing.id}
          existingImages={listing.images || []}
          disabled={saving}
          onImagesChange={refreshListing}
        />
      </div>
    </main>
  );
}

export default EditListingPage;
