import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { getSellerProfile, getSellerListings } from "../api/sellerApi";
import ListingCard from "../components/ListingCard";
import ListingGrid from "../components/ListingGrid";
import ListingGridSkeleton from "../components/ListingGridSkeleton";
import Notice from "../components/Notice";
import Pagination from "../components/Pagination";

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

  if (loadingSeller) {
    return (
      <main className="mx-auto max-w-7xl px-4 py-6">
        <div className="h-28 animate-pulse border border-slate-200 bg-slate-100" />
      </main>
    );
  }

  if (sellerError) {
    return (
      <main className="mx-auto max-w-7xl px-4 py-6">
        <Notice variant="error">
          Unable to load seller profile: {sellerError}
        </Notice>
      </main>
    );
  }

  if (!seller) {
    return (
      <main className="mx-auto max-w-7xl px-4 py-6">
        <Notice variant="warning">Seller not found.</Notice>
      </main>
    );
  }

  const sellerName =
    `${seller.firstName ?? ""} ${seller.lastName ?? ""}`.trim() || "Seller";

  return (
    <main className="mx-auto max-w-7xl px-4 py-6">
      {/* Seller profile */}
      <section className="mb-8 flex flex-col gap-4 border border-slate-300 p-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-4">
          <span className="flex h-14 w-14 shrink-0 items-center justify-center bg-blue-600 text-2xl font-medium text-white">
            {sellerName[0].toUpperCase()}
          </span>

          <div className="min-w-0">
            <h1 className="truncate text-2xl font-bold">{sellerName}</h1>

            {seller.phoneNumber && (
              <p className="text-sm text-slate-500">{seller.phoneNumber}</p>
            )}
          </div>
        </div>

        {seller.phoneNumber && (
          <a href={`tel:${seller.phoneNumber}`} className="btn btn-primary">
            Contact seller
          </a>
        )}
      </section>

      {/* Seller listings */}
      <section>
        <div className="mb-4">
          <h2 className="text-xl font-bold">Active listings</h2>

          <p className="mt-1 text-sm text-slate-500">
            {totalListings} vehicle{totalListings !== 1 && "s"}
          </p>
        </div>

        {listingsError && (
          <Notice variant="error" className="mb-4">
            Unable to load seller listings: {listingsError}
          </Notice>
        )}

        {loadingListings ? (
          <ListingGridSkeleton count={PAGE_SIZE} />
        ) : listings.length === 0 ? (
          <div className="border border-slate-300 p-8 text-center text-sm text-slate-500">
            This seller currently has no active listings.
          </div>
        ) : (
          <>
            <ListingGrid>
              {listings.map((listing) => (
                <ListingCard key={listing.id} listing={listing} />
              ))}
            </ListingGrid>

            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
              label="Seller listing pagination"
            />
          </>
        )}
      </section>
    </main>
  );
}

export default SellerProfilePage;
