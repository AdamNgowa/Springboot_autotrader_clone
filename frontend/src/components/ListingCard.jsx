import { useNavigate } from "react-router-dom";

function ListingCard({ listing, onDelete }) {
  const navigate = useNavigate();
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
    <article
      onClick={() => navigate(`/listings/${listing.id}`)}
      className="overflow-hidden rounded-lg flex flex-col gap-4 shadow-sm hover:shadow-lg border border-transparent
       hover:border-slate-300 transition-all duration-200 hover:-translate-y-1"
    >
      <div className="h-48 rounded-md bg-gray-200 flex items-center justify-center ">
        {primaryImage ? (
          <img
            src={`http://localhost:8080${primaryImage.imageUrl}`}
            alt={listing.title}
            className="w-full h-full object-cover"
          />
        ) : (
          <p className="text-gray-500">Image coming soon</p>
        )}
      </div>

      <section className="flex flex-col gap-1">
        <p className="text-lg font-medium">
          {listing.year} {listing.make} {listing.model}
        </p>

        <p className="text-sm text-gray-500">
          {listing.city} • {listing.year}
        </p>
      </section>

      <footer className="border-t pt-4">
        <strong className="text-2xl font-bold">KSh {formattedPrice}</strong>
        <div className="flex justify-between m-4">
          <button
            type="button"
            onClick={(event) => {
              event.stopPropagation();
              navigate(`/listings/${listing.id}/edit`);
            }}
            className="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700 transition-colors"
          >
            Edit
          </button>
          <button
            type="button"
            onClick={(event) => {
              event.stopPropagation();
              onDelete(listing.id);
            }}
            className="px-4 py-2 rounded-md bg-red-600 text-white hover:bg-red-700 transition-colors"
          >
            Delete
          </button>
        </div>
      </footer>
    </article>
  );
}

export default ListingCard;
