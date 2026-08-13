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

const PAGE_SIZE = 5;

function HomePage() {
  const [listings, setListings] = useState([]);
  const [filters, setFilters] = useState(INITIAL_FILTERS);
  const [activeFilters, setActiveFilters] = useState(filters);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalListings, setTotalListings] = useState(0);

  const pageNumbers = Array.from({ length: totalPages }, (_, index) => index);

  useEffect(() => {
    async function loadListings() {
      setLoading(true);
      setError(null);

      try {
        const data = await getListings({
          ...activeFilters,
          page: currentPage,
          size: PAGE_SIZE,
        });

        setListings(data.content);
        setTotalListings(data.totalElements);
        setTotalPages(data.totalPages);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadListings();
  }, [activeFilters, currentPage]);

  function applyFilters() {
    setActiveFilters({ ...filters });
    setCurrentPage(0);
  }

  function resetFilters() {
    setFilters(INITIAL_FILTERS);
    setActiveFilters(INITIAL_FILTERS);
    setCurrentPage(0);
  }

  function goToPreviousPage() {
    if (currentPage > 0) {
      setCurrentPage(currentPage - 1);
    }
  }

  function goToNextPage() {
    if (currentPage < totalPages - 1) {
      setCurrentPage(currentPage + 1);
    }
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
            <p className="mb-4 text-sm text-gray-500">Loading listings...</p>
          )}

          {!error && listings.length === 0 ? (
            <p className="text-center text-gray-500">
              No listings match your current filters.
            </p>
          ) : (
            <>
              <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                {listings.map((listing) => (
                  <ListingCard key={listing.id} listing={listing} />
                ))}
              </div>

              {totalPages > 1 && (
                <nav
                  className="mt-8 flex flex-wrap items-center justify-center gap-2"
                  aria-label="Pagination"
                >
                  <button
                    type="button"
                    onClick={goToPreviousPage}
                    disabled={currentPage === 0}
                    className="rounded-lg border px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    Previous
                  </button>

                  {pageNumbers.map((pageNumber) => (
                    <button
                      key={pageNumber}
                      type="button"
                      onClick={() => setCurrentPage(pageNumber)}
                      aria-current={
                        currentPage === pageNumber ? "page" : undefined
                      }
                      className={`rounded-lg border px-4 py-2 ${
                        currentPage === pageNumber
                          ? "bg-blue-500 font-medium text-white"
                          : "hover:bg-gray-100"
                      }`}
                    >
                      {pageNumber + 1}
                    </button>
                  ))}

                  <button
                    type="button"
                    onClick={goToNextPage}
                    disabled={currentPage === totalPages - 1}
                    className="rounded-lg border px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    Next
                  </button>
                </nav>
              )}
            </>
          )}
        </section>
      </div>
    </main>
  );
}

export default HomePage;
