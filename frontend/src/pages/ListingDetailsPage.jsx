import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { getListing } from "../api/listingApi";

function ListingDetailsPage() {
  const { id } = useParams();

  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  //When the id changes,trigger the code inside this effect
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

  if (loading) {
    return <p>Load listing...</p>;
  }

  if (error) {
    return <p>Error:{error}</p>;
  }

  if (!listing) {
    return <p>Listing not found.</p>;
  }

  const formattedPrice = new Intl.NumberFormat().format(listing.price);
  // 1. Declare a variable to hold the primary image object (or undefined if not found).
  const primaryImage =
    // 2. Safely access the 'images' array on the 'listing' object.
    //    The optional chaining operator (?.) checks if 'images' exists first;
    //    if listing.images is null or undefined, execution stops here and returns undefined
    //    instead of throwing a TypeError crash.
    listing.images
      // 3. Call the built-in JavaScript Array method .find() to loop through the images.
      ?.find(
        // 4. Pass an inline arrow function callback that receives each individual 'image' object.
        (image) =>
          // 5. Evaluate the 'primaryImage' boolean property on the current image.
          //    .find() checks this truthiness: the moment it encounters an image where
          //    image.primaryImage === true, it immediately stops looping and returns
          //    that exact image object. If no image has primaryImage: true, it returns undefined.
          image.primaryImage,
      );

  return (
    <main className="max-w-6xl mx-auto p-6">
      <div className="mb-8 h-96 overflow-hidden rounded-lg bg-gray-200">
        {primaryImage ? (
          <img
            src={`http://localhost:8080${primaryImage.imageUrl}`}
            alt={listing.title}
            className="h-full w-full object-cover"
          />
        ) : (
          <div className="flex h-full items-center justify-center text-gray-500">
            No image available
          </div>
        )}
      </div>
      <section>
        <h1 className="text-4xl font-bold">{listing.title}</h1>

        <p className="mt-4 text-3xl font-bold text-blue-700">
          KSh {formattedPrice}
        </p>

        <p className="mt-2 text-gray-600">{listing.city}</p>
      </section>
      <section className="mt-10">
        <h2 className="mb-3 text-2xl font-semibold">Description</h2>

        <p className="leading-7 text-gray-700">{listing.description}</p>
      </section>
      <section className="mt-10">
        <h2 className="mb-4 text-2xl font-semibold">Specifications</h2>

        <div className="grid grid-cols-2 gap-4">
          <p>
            <strong>Year:</strong> {listing.year}
          </p>

          <p>
            <strong>Make:</strong> {listing.make}
          </p>

          <p>
            <strong>Model:</strong> {listing.model}
          </p>

          <p>
            <strong>Mileage:</strong> {listing.mileage.toLocaleString()} km
          </p>

          <p>
            <strong>Fuel:</strong> {listing.fuelType}
          </p>

          <p>
            <strong>Transmission:</strong> {listing.transmission}
          </p>

          <p>
            <strong>Body Type:</strong> {listing.bodyType}
          </p>
        </div>
      </section>
    </main>
  );
}
export default ListingDetailsPage;
