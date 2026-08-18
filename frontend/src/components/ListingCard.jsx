import { Link, useNavigate } from "react-router-dom";
import { getImageUrl } from "../utils/getImageUrl";

function ListingCard({ listing, onDelete, showOwnerActions = false }) {
  const navigate = useNavigate();
  const formattedPrice = new Intl.NumberFormat().format(listing.price);

  // Find the primary image, if one exists.
  const primaryImage = listing.images?.find((image) => image.primaryImage);

  return (
    <article className="overflow-hidden rounded-lg border border-transparent shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-slate-300 hover:shadow-lg">
      <Link
        to={`/listings/${listing.id}`}
        className="block focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
      >
        <div className="flex h-48 items-center justify-center bg-gray-200">
          {primaryImage ? (
            <img
              src={getImageUrl(primaryImage.imageUrl)}
              alt={listing.title}
              className="h-full w-full object-cover"
            />
          ) : (
            <div className="px-4 text-center">
              <p className="text-sm font-medium text-gray-600">
                No image available
              </p>
            </div>
          )}
        </div>

        <div className="flex flex-col gap-4 p-4">
          <section className="flex flex-col gap-1">
            <p className="text-lg font-medium">
              {listing.year} {listing.make} {listing.model}
            </p>

            <p className="text-sm text-gray-500">{listing.city}</p>
          </section>

          <section>
            <strong className="text-2xl font-bold">KSh {formattedPrice}</strong>
          </section>
        </div>
      </Link>

      {showOwnerActions && (
        <footer className="border-t p-4">
          <div className="flex justify-between">
            <button
              type="button"
              onClick={() => navigate(`/listings/${listing.id}/edit`)}
              className="rounded-md bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
            >
              Edit
            </button>

            <button
              type="button"
              onClick={() => onDelete(listing.id)}
              className="rounded-md bg-red-600 px-4 py-2 text-white transition-colors hover:bg-red-700"
            >
              Delete
            </button>
          </div>
        </footer>
      )}
    </article>
  );
}

export default ListingCard;
