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

const PAGE_SIZE = 6;

// Filters shown as removable chips. Sort has its own dropdown.
const FILTER_KEYS = [
  "make",
  "city",
  "minPrice",
  "maxPrice",
  "bodyType",
  "fuelType",
  "transmission",
];

const SORT_OPTIONS = [
  { value: "createdAt,desc", label: "Newest first" },
  { value: "createdAt,asc", label: "Oldest first" },
  { value: "price,asc", label: "Price: low to high" },
  { value: "price,desc", label: "Price: high to low" },
  { value: "year,desc", label: "Year: newest" },
  { value: "year,asc", label: "Year: oldest" },
];

// Turns a filter key and value into the text shown on its chip.
function chipLabel(key, value) {
  if (key === "minPrice") return `Min KSh ${Number(value).toLocaleString()}`;
  if (key === "maxPrice") return `Max KSh ${Number(value).toLocaleString()}`;
  return value;
}

function HomePage() {
  const [listings, setListings] = useState([]);
  const [filters, setFilters] = useState(INITIAL_FILTERS);
  const [activeFilters, setActiveFilters] = useState(filters);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalListings, setTotalListings] = useState(0);

  // Controls the mobile filter sheet. Ignored on desktop, where the sidebar is always shown.
  const [filtersOpen, setFiltersOpen] = useState(false);

  const pageNumbers = Array.from({ length: totalPages }, (_, index) => index);

  // The chips are derived from the filters that are actually applied.
  const activeChips = FILTER_KEYS.filter(
    (key) => activeFilters[key] !== "",
  ).map((key) => ({ key, label: chipLabel(key, activeFilters[key]) }));

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

  // Stop the page behind the sheet from scrolling while it is open.
  useEffect(() => {
    document.body.style.overflow = filtersOpen ? "hidden" : "";

    return () => {
      document.body.style.overflow = "";
    };
  }, [filtersOpen]);

  function applyFilters() {
    setActiveFilters({ ...filters });
    setCurrentPage(0);
    setFiltersOpen(false);
  }

  function resetFilters() {
    setFilters(INITIAL_FILTERS);
    setActiveFilters(INITIAL_FILTERS);
    setCurrentPage(0);
  }

  // Removing a chip clears that one filter in both the form and the applied state.
  function removeFilter(key) {
    setFilters((current) => ({ ...current, [key]: "" }));
    setActiveFilters((current) => ({ ...current, [key]: "" }));
    setCurrentPage(0);
  }

  // Sorting applies immediately; it does not wait for "Apply filters".
  function changeSort(sort) {
    setFilters((current) => ({ ...current, sort }));
    setActiveFilters((current) => ({ ...current, sort }));
    setCurrentPage(0);
  }

  function goToPage(page) {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const showSkeleton = loading && listings.length === 0;
  const showEmpty = !loading && !error && listings.length === 0;

  return (
    <main className="mx-auto max-w-7xl px-4 py-6">
      <div className="lg:grid lg:grid-cols-[280px_1fr] lg:gap-8">
        {/* Search filters: sidebar on desktop, bottom sheet on mobile */}
        <aside>
          <SearchFilters
            filters={filters}
            setFilters={setFilters}
            onApply={applyFilters}
            onReset={resetFilters}
            open={filtersOpen}
            onClose={() => setFiltersOpen(false)}
          />
        </aside>

        {/* Listings section */}
        <section className="min-w-0">
          <h1 className="mb-4 text-2xl font-bold">Latest vehicles</h1>

          {/* Toolbar */}
          <div className="mb-4 flex gap-2">
            <button
              type="button"
              onClick={() => setFiltersOpen(true)}
              className="btn btn-outline lg:hidden"
            >
              Filters
              {activeChips.length > 0 && (
                <span className="bg-blue-600 px-1.5 text-xs text-white">
                  {activeChips.length}
                </span>
              )}
            </button>

            <select
              value={activeFilters.sort}
              onChange={(event) => changeSort(event.target.value)}
              aria-label="Sort listings"
              className="field flex-1 sm:ml-auto sm:w-auto sm:flex-none"
            >
              {SORT_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          {/* Active filter chips */}
          {activeChips.length > 0 && (
            <div className="mb-4 flex flex-wrap gap-2">
              {activeChips.map((chip) => (
                <button
                  key={chip.key}
                  type="button"
                  onClick={() => removeFilter(chip.key)}
                  aria-label={`Remove filter ${chip.label}`}
                  className="flex items-center gap-1.5 border border-slate-300 bg-white px-2.5 py-1 text-sm hover:border-slate-900"
                >
                  {chip.label}
                  <span aria-hidden="true">×</span>
                </button>
              ))}
            </div>
          )}

          {!error && (
            <p className="mb-4 text-sm text-slate-500">
              Showing {totalListings} vehicle{totalListings !== 1 && "s"}
            </p>
          )}

          {error && (
            <p
              role="alert"
              className="mb-4 border border-red-300 bg-red-50 p-3 text-sm text-red-700"
            >
              Unable to load listings. Please try again
            </p>
          )}

          {loading && (
            <p className="mb-4 text-sm text-slate-500">Loading listings...</p>
          )}

          {/* First load: placeholder boxes so the layout does not jump */}
          {showSkeleton && (
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3">
              {Array.from({ length: PAGE_SIZE }, (_, index) => (
                <div
                  key={index}
                  className="h-72 animate-pulse border border-slate-200 bg-slate-100"
                />
              ))}
            </div>
          )}

          {showEmpty && (
            <div className="border border-slate-300 p-8 text-center">
              <p className="font-medium">No listings match your filters</p>
              <p className="mt-1 text-sm text-slate-500">
                Try removing a filter or widening the price range.
              </p>
              <button
                type="button"
                onClick={resetFilters}
                className="btn btn-outline mt-4"
              >
                Reset filters
              </button>
            </div>
          )}

          {listings.length > 0 && (
            <>
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3">
                {listings.map((listing) => (
                  <ListingCard key={listing.id} listing={listing} />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <nav
                  className="mt-8 flex items-center justify-center gap-2"
                  aria-label="Pagination"
                >
                  <button
                    type="button"
                    onClick={() => goToPage(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="btn btn-outline"
                  >
                    Previous
                  </button>

                  {/* Small screens: a compact counter instead of every page number */}
                  <span className="px-2 text-sm text-slate-600 sm:hidden">
                    Page {currentPage + 1} of {totalPages}
                  </span>

                  <div className="hidden gap-2 sm:flex">
                    {pageNumbers.map((pageNumber) => (
                      <button
                        key={pageNumber}
                        type="button"
                        onClick={() => goToPage(pageNumber)}
                        aria-current={
                          currentPage === pageNumber ? "page" : undefined
                        }
                        className={`h-10 min-w-10 border px-3 text-sm transition-colors ${
                          currentPage === pageNumber
                            ? "border-blue-600 bg-blue-600 font-medium text-white"
                            : "border-slate-300 hover:border-slate-900 hover:bg-slate-50"
                        }`}
                      >
                        {pageNumber + 1}
                      </button>
                    ))}
                  </div>

                  <button
                    type="button"
                    onClick={() => goToPage(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                    className="btn btn-outline"
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
