import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getListing, deleteListing } from "../api/listingApi";
import { createOrGetConversation } from "../api/messagingApi";
import { useAuth } from "../hooks/useAuth";
import SpecificationCard from "../components/SpecificationCard";
import ImageGallery from "../components/ImageGallery";
import SellerCard from "../components/SellerCard";
import ListingManagement from "../components/ListingManagement";
import Notice from "../components/Notice";

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
      <main className="mx-auto max-w-6xl px-4 py-6">
        <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
          <div className="aspect-[4/3] animate-pulse border border-slate-200 bg-slate-100 md:aspect-[16/10]" />
          <div className="h-48 animate-pulse border border-slate-200 bg-slate-100" />
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main className="mx-auto max-w-6xl px-4 py-6">
        <Notice variant="error">Error: {error}</Notice>
      </main>
    );
  }

  if (!listing) {
    return (
      <main className="mx-auto max-w-6xl px-4 py-6">
        <Notice variant="warning">Listing not found.</Notice>
      </main>
    );
  }

  const isOwner = user && listing.seller && user.id === listing.seller.id;

  const formattedPrice = new Intl.NumberFormat().format(listing.price);

  // The sticky bottom bar only exists for visitors who can message the seller.
  const showMessageBar = !isOwner && Boolean(listing.seller);

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
    <main
      className={`mx-auto max-w-6xl px-4 py-6 ${
        showMessageBar ? "pb-24 lg:pb-6" : ""
      }`}
    >
      {/*
       * Grid placement: on desktop the aside spans both rows of column 2.
       * On mobile everything stacks in DOM order: gallery, summary, details.
       */}
      <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px] lg:gap-8">
        {/* Gallery */}
        <div className="min-w-0 lg:col-start-1 lg:row-start-1">
          <ImageGallery
            images={listing.images}
            selectedImage={selectedImage}
            setSelectedImage={setSelectedImage}
            title={listing.title}
          />
        </div>

        {/* Summary + seller */}
        <aside className="min-w-0 space-y-4 lg:sticky lg:top-20 lg:col-start-2 lg:row-span-2 lg:row-start-1 lg:self-start">
          <div className="border border-slate-300 p-4">
            <h1 className="text-2xl font-bold text-slate-900">
              {listing.title}
            </h1>

            <p className="mt-1 text-sm text-slate-500">
              {listing.year} {listing.make} {listing.model} · {listing.city}
            </p>

            <p className="mt-3 text-3xl font-bold text-blue-700">
              KSh {formattedPrice}
            </p>
          </div>

          {isOwner && (
            <ListingManagement
              onEdit={handleEdit}
              onDelete={handleDelete}
              deleting={deleting}
            />
          )}

          <SellerCard
            seller={listing.seller}
            isOwner={isOwner}
            onMessage={handleMessageSeller}
            messaging={messagingSeller}
            error={messageError}
          />
        </aside>

        {/* Description + specifications */}
        <div className="min-w-0 space-y-8 lg:col-start-1 lg:row-start-2">
          <section>
            <h2 className="mb-3 text-lg font-semibold">Description</h2>

            <p className="whitespace-pre-line leading-7 text-slate-700">
              {listing.description}
            </p>
          </section>

          <section>
            <h2 className="mb-3 text-lg font-semibold">Specifications</h2>

            <div className="grid grid-cols-2 gap-3 md:grid-cols-3">
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
                label="Body type"
                value={formatEnum(listing.bodyType)}
              />

              <SpecificationCard label="Location" value={listing.city} />
            </div>
          </section>
        </div>
      </div>

      {/*
       * Mobile-only sticky action bar. pr-24 leaves room for the floating
       * messages button (right-6 + 56px wide) so the two never overlap.
       */}
      {showMessageBar && (
        <div className="fixed inset-x-0 bottom-0 z-40 flex flex-col gap-1 border-t border-slate-300 bg-white py-3 pl-4 pr-24 lg:hidden">
          {messageError && (
            <p className="text-xs text-red-600">
              Unable to start conversation: {messageError}
            </p>
          )}

          <button
            type="button"
            onClick={handleMessageSeller}
            disabled={messagingSeller}
            className="btn btn-primary w-full"
          >
            {messagingSeller ? "Starting conversation..." : "Message seller"}
          </button>
        </div>
      )}
    </main>
  );
}

export default ListingDetailsPage;
