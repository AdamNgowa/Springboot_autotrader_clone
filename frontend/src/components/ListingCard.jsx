function ListingCard({ listing }) {
  const formattedPrice = new Intl.NumberFormat().format(listing.price);
  const primaryImage = listing.images?.find((image) => image.primaryImage);
  return (
    <article className="border rounded-lg p-4 flex flex-col gap-4">
      <div className="h-48 rounded-md bg-gray-200 flex items-center justify-center">
        {primaryImage ? (
          <img
            src={`http://localhost:8080${primaryImage.imageUrl}`}
            alt={listing.title}
          />
        ) : (
          <p className="text-gray-500">Image coming soon</p>
        )}
      </div>

      <section>
        <p>
          {listing.year} {listing.make} {listing.model}
        </p>

        <p>
          {listing.city} • {listing.year}
        </p>
      </section>

      <footer>
        <strong className="text-2xl font-bold">KSh {formattedPrice}</strong>
      </footer>
    </article>
  );
}

export default ListingCard;
