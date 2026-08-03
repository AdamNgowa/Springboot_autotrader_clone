import { useNavigate } from "react-router-dom";

function ListingCard({ listing }) {
  const navigate = useNavigate();
  const formattedPrice = new Intl.NumberFormat().format(listing.price);
  const primaryImage = listing.images?.find((image) => image.primaryImage);
  return (
    <article
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
            onClick={() => navigate(`/listings/${listing.id}/edit`)}
            className="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700 transition-colors"
          >
            Edit
          </button>
          <button
            type="button"
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
