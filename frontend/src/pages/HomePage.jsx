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
  const [activeFilters, setActiveFilters] = useState(filters);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalListings, setTotalListings] = useState(0);

  useEffect(() => {
    async function loadListings() {
      setLoading(true);
      setError(null);

      try {
        const data = await getListings(activeFilters);
        setListings(data.content);
        setTotalListings(data.totalElements);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadListings();
  }, [activeFilters]);

  function applyFilters() {
    setActiveFilters({ ...filters });
  }

  function resetFilters() {
    setFilters(INITIAL_FILTERS);
    setActiveFilters(INITIAL_FILTERS);
  }

  if (loading && listings.length === 0) {
    return (
      <main className="max-w-6xl mx-auto p-6">
        <p>Loading listings...</p>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-7xl p-4 md:p-6">
      <div className="grid gap-8 lg:grid-cols-[280px_1fr]">
        <aside>
          <SearchFilters
            filters={filters}
            setFilters={setFilters}
            onApply={applyFilters}
            onReset={resetFilters}
          />
        </aside>
        <section>
          <h1 className="mb-6 text-3xl font-bold">Latest Vehicles</h1>

          <p className="mb-6 text-gray-500">
            Showing {totalListings} vehicle
            {totalListings !== 1 && "s"}
          </p>

          {error && (
            <p className="mb-4 text-red-600">
              Unable to load listings. Please try again
            </p>
          )}

          {loading && (
            <p className="mb-4 text-sm text-gray-500">Searching...</p>
          )}

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
        </section>
      </div>
    </main>
  );
}

export default HomePage;
