import { useEffect, useState } from "react";
import { getListings } from "../api/listingApi";
import ListingCard from "../components/ListingCard";
import SearchFilters from "../components/SearchFilters";

const INITIAL_FILTERS = {
  make: "",
  city: "",
  minPrice: "",
  maxPrice: "",
  bodyType: "",
  fuelType: "",
  transmission: "",
  year: "",
  sort: "createdAt,desc",
};

function HomePage() {
  const [listings, setListings] = useState([]);
  const [filters, setFilters] = useState(INITIAL_FILTERS);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [debouncedFilters, setDebouncedFilters] = useState(filters);
  const [totalListings, setTotalListings] = useState(0);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedFilters(filters);
    }, 2000);

    return () => clearTimeout(timer);
  }, [filters]);

  useEffect(() => {
    async function loadListings() {
      setLoading(true);
      setError(null);

      try {
        const data = await getListings(debouncedFilters);
        setListings(data.content);
        setTotalListings(data.totalElements);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadListings();
  }, [debouncedFilters]);

  function resetFilters() {
    setFilters(INITIAL_FILTERS);
  }

  if (loading && listings.length === 0) {
    return (
      <main className="max-w-6xl mx-auto p-6">
        <p>Loading listings...</p>
      </main>
    );
  }

  return (
    <main className="max-w-6xl mx-auto p-6">
      <h1 className="mb-6 text-3xl font-bold">Latest Vehicles</h1>

      <p className="mb-6 text-gray-500">
        Showing {totalListings} vehicle
        {totalListings !== 1 && "s"}
      </p>

      <SearchFilters
        filters={filters}
        setFilters={setFilters}
        onReset={resetFilters}
      />

      {error && (
        <p className="mb-4 text-red-600">
          Unable to load listings. Please try again
        </p>
      )}

      {loading && <p className="mb-4 text-sm text-gray-500">Searching...</p>}

      {/* No listings message only appears when there is no error and no listings matching the criteria */}
      {!error && listings.length === 0 ? (
        <p className="text-center text-gray-500">
          No listings match your current filters.
        </p>
      ) : (
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          {listings.map((listing) => (
            <ListingCard key={listing.id} listing={listing} />
          ))}
        </div>
      )}
    </main>
  );
}

export default HomePage;
