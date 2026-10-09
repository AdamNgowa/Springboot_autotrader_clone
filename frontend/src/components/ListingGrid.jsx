function ListingGrid({ children, withSidebar = false }) {
  const columns = withSidebar
    ? "sm:grid-cols-2 xl:grid-cols-3"
    : "sm:grid-cols-2 lg:grid-cols-3";

  return <div className={`grid grid-cols-1 gap-6 ${columns}`}>{children}</div>;
}

export default ListingGrid;
