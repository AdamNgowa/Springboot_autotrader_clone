import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { updateListing, getListing } from "../api/listingApi";
import ListingForm from "../components/ListingForm";
import ImageManager from "../components/ImageManager";
import Notice from "../components/Notice";
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
    return (
      <main className="mx-auto max-w-4xl px-4 py-6">
        <div className="h-96 animate-pulse border border-slate-200 bg-slate-100" />
      </main>
    );
  }

  if (error && !listing) {
    return (
      <main className="mx-auto max-w-4xl px-4 py-6">
        <Notice variant="error">Error: {error}</Notice>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-4xl px-4 py-6">
      <div className="mb-6 flex items-center justify-between gap-4">
        <h1 className="text-2xl font-bold">Edit listing</h1>

        <button
          type="button"
          onClick={() => navigate(`/listings/${id}`)}
          className="btn btn-outline"
        >
          View listing
        </button>
      </div>

      {error && (
        <Notice variant="error" className="mb-4">
          {error}
        </Notice>
      )}

      {success && (
        <Notice variant="success" className="mb-4">
          {success}
        </Notice>
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
