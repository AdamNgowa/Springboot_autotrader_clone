import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { getSellerProfile, getSellerListings } from "../api/sellerApi";
import ListingCard from "../components/ListingCard";

const PAGE_SIZE = 6;

function SellerProfilePage() {
  const { id } = useParams();

  const [seller, setSeller] = useState(null);
  const [listings, setListings] = useState([]);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalListings, setTotalListings] = useState(0);

  const [loadingSeller, setLoadingSeller] = useState(true);
  const [loadingListings, setLoadingListings] = useState(true);

  const [sellerError, setSellerError] = useState(null);
  const [listingsError, setListingsError] = useState(null);

  useEffect(() => {
    async function loadSeller() {
      setLoadingSeller(true);
      setSellerError(null);

      try {
        const data = await getSellerProfile(id);
        setSeller(data);
      } catch (error) {
        setSellerError(error.message);
      } finally {
        setLoadingSeller(false);
      }
    }

    loadSeller();
  }, [id]);

  useEffect(() => {
    async function loadListings() {
      setLoadingListings(true);
      setListingsError(null);

      try {
        const data = await getSellerListings(id, {
          page: currentPage,
          size: PAGE_SIZE,
          sort: "createdAt,desc",
        });

        setListings(data.content ?? []);
        setTotalPages(data.totalPages ?? 0);
        setTotalListings(data.totalElements ?? 0);
      } catch (error) {
        setListingsError(error.message);
      } finally {
        setLoadingListings(false);
      }
    }

    loadListings();
  }, [id, currentPage]);

  function goToPreviousPage() {
    if (currentPage > 0) {
      setCurrentPage((page) => page - 1);
    }
  }

  function goToNextPage() {
    if (currentPage < totalPages - 1) {
      setCurrentPage((page) => page + 1);
    }
  }

  if (loadingSeller) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <p className="text-center text-slate-500">Loading seller profile...</p>
      </main>
    );
  }

  if (sellerError) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <div className="rounded-lg bg-red-100 p-4 text-red-700">
          Unable to load seller profile: {sellerError}
        </div>
      </main>
    );
  }

  if (!seller) {
    return (
      <main className="mx-auto max-w-6xl p-6">
        <div className="rounded-lg bg-yellow-100 p-4 text-yellow-700">
          Seller not found.
        </div>
      </main>
    );
  }

  const sellerName =
    `${seller.firstName ?? ""} ${seller.lastName ?? ""}`.trim() || "Seller";

  return (
    <main className="mx-auto max-w-6xl p-6">
      {/* Seller profile */}
      <section className="mb-10 rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="mb-2 text-sm font-medium uppercase tracking-wide text-blue-600">
              Seller Profile
            </p>

            <h1 className="text-3xl font-bold text-slate-900">{sellerName}</h1>

            {seller.phoneNumber && (
              <p className="mt-3 text-slate-600">Phone: {seller.phoneNumber}</p>
            )}
          </div>

          {seller.phoneNumber && (
            <a
              href={`tel:${seller.phoneNumber}`}
              className="inline-flex w-fit rounded-lg bg-blue-600 px-5 py-3 font-medium text-white transition hover:bg-blue-700"
            >
              Contact Seller
            </a>
          )}
        </div>
      </section>

      {/* Seller listings */}
      <section>
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Active Listings</h2>

          <p className="mt-1 text-slate-500">
            {totalListings} vehicle
            {totalListings !== 1 ? "s" : ""}
          </p>
        </div>

        {listingsError && (
          <div className="mb-6 rounded-lg bg-red-100 p-4 text-red-700">
            Unable to load seller listings: {listingsError}
          </div>
        )}

        {loadingListings ? (
          <p className="text-slate-500">Loading listings...</p>
        ) : listings.length === 0 ? (
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-8 text-center">
            <p className="text-slate-600">
              This seller currently has no active listings.
            </p>
          </div>
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
                aria-label="Seller listing pagination"
              >
                <button
                  type="button"
                  onClick={goToPreviousPage}
                  disabled={currentPage === 0}
                  className="rounded-lg border px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Previous
                </button>

                {Array.from({ length: totalPages }, (_, index) => index).map(
                  (pageNumber) => (
                    <button
                      key={pageNumber}
                      type="button"
                      onClick={() => setCurrentPage(pageNumber)}
                      aria-current={
                        currentPage === pageNumber ? "page" : undefined
                      }
                      className={`rounded-lg border px-4 py-2 ${
                        currentPage === pageNumber
                          ? "bg-blue-600 font-medium text-white"
                          : "hover:bg-gray-100"
                      }`}
                    >
                      {pageNumber + 1}
                    </button>
                  ),
                )}

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
    </main>
  );
}

export default SellerProfilePage;
