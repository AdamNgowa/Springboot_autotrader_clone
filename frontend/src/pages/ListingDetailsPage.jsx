import { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { getListing, deleteListing } from "../api/listingApi";
import { createOrGetConversation } from "../api/messagingApi";
import { useAuth } from "../hooks/useAuth";
import SpecificationCard from "../components/SpecificationCard";
import ImageGallery from "../components/ImageGallery";

function ListingDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedImage, setSelectedImage] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [messagingSeller, setMessagingSeller] = useState(false);
  const [messageError, setMessageError] = useState(null);

  useEffect(() => {
    async function loadListing() {
      try {
        const data = await getListing(id);

        setListing(data);

        const primary =
          data.images?.find((image) => image.primaryImage) ??
          data.images?.[0] ??
          null;

        setSelectedImage(primary);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadListing();
  }, [id]);

  if (loading) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <p className="text-center text-slate-500">Loading listing...</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <div className="rounded-lg bg-red-100 p-4 text-red-700">
          Error: {error}
        </div>
      </main>
    );
  }

  if (!listing) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <div className="rounded-lg bg-yellow-100 p-4 text-yellow-700">
          Listing not found.
        </div>
      </main>
    );
  }

  const isOwner = user && listing.seller && user.id === listing.seller.id;

  const formattedPrice = new Intl.NumberFormat().format(listing.price);

  function handleEdit() {
    navigate(`/listings/${listing.id}/edit`);
  }

  async function handleDelete() {
    const confirmed = window.confirm(
      "Are you sure you want to delete this listing? This action will remove it from the marketplace.",
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeleting(true);

      await deleteListing(listing.id);

      navigate("/my-listings");
    } catch (error) {
      setError(error.message);
      setDeleting(false);
    }
  }

  async function handleMessageSeller() {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    if (messagingSeller) {
      return;
    }

    try {
      setMessagingSeller(true);
      setMessageError(null);

      const conversation = await createOrGetConversation(listing.id);

      navigate(`/conversations/${conversation.id}`);
    } catch (error) {
      setMessageError(error.message);
      setMessagingSeller(false);
    }
  }

  function formatEnum(value) {
    if (!value) return "";

    return value
      .toLowerCase()
      .replaceAll("_", " ")
      .replace(/\b\w/g, (letter) => letter.toUpperCase());
  }

  return (
    <main className="max-w-6xl mx-auto flex flex-col gap-10 p-6">
      <ImageGallery
        images={listing.images}
        selectedImage={selectedImage}
        setSelectedImage={setSelectedImage}
        title={listing.title}
      />

      <section className="flex flex-col gap-2">
        <h1 className="text-4xl font-bold text-slate-900">{listing.title}</h1>

        <p className="text-3xl font-bold text-blue-700">KSh {formattedPrice}</p>

        <p className="text-lg text-slate-500">{listing.city}</p>
      </section>

      {isOwner && (
        <section className="rounded-xl border border-blue-200 bg-blue-50 p-5">
          <h2 className="text-lg font-semibold text-slate-900">
            Listing Management
          </h2>

          <p className="mt-1 text-sm text-slate-600">
            You are the owner of this listing.
          </p>

          <div className="mt-4 flex flex-wrap gap-3">
            <button
              type="button"
              onClick={handleEdit}
              disabled={deleting}
              className="rounded-lg bg-blue-600 px-5 py-3 font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Edit Listing
            </button>

            <button
              type="button"
              onClick={handleDelete}
              disabled={deleting}
              className="rounded-lg bg-red-600 px-5 py-3 font-medium text-white transition hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {deleting ? "Deleting..." : "Delete Listing"}
            </button>
          </div>
        </section>
      )}

      <section>
        <h2 className="mb-3 text-2xl font-semibold">Description</h2>

        <p className="leading-8 text-slate-700">{listing.description}</p>
      </section>

      <section>
        <h2 className="mb-5 text-2xl font-semibold">Specifications</h2>

        <div className="grid grid-cols-2 gap-4 md:grid-cols-3">
          <SpecificationCard label="Year" value={listing.year} />
          <SpecificationCard label="Make" value={listing.make} />
          <SpecificationCard label="Model" value={listing.model} />

          <SpecificationCard
            label="Mileage"
            value={`${listing.mileage.toLocaleString()} km`}
          />

          <SpecificationCard
            label="Fuel"
            value={formatEnum(listing.fuelType)}
          />

          <SpecificationCard
            label="Transmission"
            value={formatEnum(listing.transmission)}
          />

          <SpecificationCard
            label="Body Type"
            value={formatEnum(listing.bodyType)}
          />

          <SpecificationCard label="Location" value={listing.city} />
        </div>
      </section>

      <section>
        <h2 className="mb-4 text-2xl font-semibold">Seller</h2>

        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          {listing.seller ? (
            <div className="flex flex-col gap-4">
              <div>
                <Link
                  to={`/sellers/${listing.seller.id}`}
                  className="text-lg font-semibold text-blue-600 hover:text-blue-700 hover:underline"
                >
                  {listing.seller.firstName} {listing.seller.lastName}
                </Link>

                <p className="mt-2 text-slate-600">
                  Phone: {listing.seller.phoneNumber || "Not provided"}
                </p>
              </div>

              {!isOwner && (
                <div className="flex flex-wrap gap-3">
                  {listing.seller.phoneNumber && (
                    <a
                      href={`tel:${listing.seller.phoneNumber}`}
                      className="inline-flex w-fit items-center rounded-lg bg-blue-600 px-5 py-3 font-medium text-white transition hover:bg-blue-700"
                    >
                      Contact Seller
                    </a>
                  )}

                  <button
                    type="button"
                    onClick={handleMessageSeller}
                    disabled={messagingSeller}
                    className="inline-flex w-fit items-center rounded-lg border border-blue-600 px-5 py-3 font-medium text-blue-600 transition hover:bg-blue-50 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    {messagingSeller
                      ? "Starting conversation..."
                      : "Message Seller"}
                  </button>
                </div>
              )}

              {messageError && (
                <p className="text-sm text-red-600">
                  Unable to start conversation: {messageError}
                </p>
              )}
            </div>
          ) : (
            <p className="text-slate-500">
              Seller information is not available.
            </p>
          )}
        </div>
      </section>
    </main>
  );
}

export default ListingDetailsPage;
