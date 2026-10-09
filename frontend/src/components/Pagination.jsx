function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  label = "Pagination",
}) {
  if (totalPages <= 1) {
    return null;
  }

  function goTo(page) {
    onPageChange(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const pageNumbers = Array.from({ length: totalPages }, (_, index) => index);

  return (
    <nav
      className="mt-8 flex items-center justify-center gap-2"
      aria-label={label}
    >
      <button
        type="button"
        onClick={() => goTo(currentPage - 1)}
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
            onClick={() => goTo(pageNumber)}
            aria-current={currentPage === pageNumber ? "page" : undefined}
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
        onClick={() => goTo(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
        className="btn btn-outline"
      >
        Next
      </button>
    </nav>
  );
}

export default Pagination;
