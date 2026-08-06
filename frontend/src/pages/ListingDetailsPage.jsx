import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { getListing } from "../api/listingApi";
import SpecificationCard from "../components/SpecificationCard";
import ImageGallery from "../components/ImageGallery";

function ListingDetailsPage() {
  const { id } = useParams();

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedImage, setSelectedImage] = useState(null);

  //When the id changes,trigger the code inside this effect
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

  const formattedPrice = new Intl.NumberFormat().format(listing.price);

  function formatEnum(value) {
    if (!value) return "";

    return value
      .toLowerCase()
      .replaceAll("_", " ")
      .replace(/\b\w/g, (letter) => letter.toUpperCase());
  }

  return (
    <main className="max-w-6xl mx-auto space-y-10  p-6">
      <ImageGallery
        images={listing.images}
        selectedImage={selectedImage}
        setSelectedImage={setSelectedImage}
        title={listing.title}
      />

      <section className="space-y-2">
        <h1 className="text-4xl font-bold text-slate-900">{listing.title}</h1>

        <p className="text-3xl font-bold text-blue-700">KSh {formattedPrice}</p>

        <p className="text-lg text-slate-500">{listing.city}</p>
      </section>
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
          <h3 className="text-lg font-semibold">Vehicle Marketplace Seller</h3>

          <p className="mt-2 text-slate-600">
            Contact information will be available in a future update.
          </p>
        </div>
      </section>
    </main>
  );
}
export default ListingDetailsPage;
