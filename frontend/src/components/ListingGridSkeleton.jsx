import ListingGrid from "./ListingGrid";

// Placeholder boxes shown during the first load so the layout does not jump.
function ListingGridSkeleton({ count = 6, withSidebar = false }) {
  return (
    <ListingGrid withSidebar={withSidebar}>
      {Array.from({ length: count }, (_, index) => (
        <div
          key={index}
          className="h-72 animate-pulse border border-slate-200 bg-slate-100"
        />
      ))}
    </ListingGrid>
  );
}

export default ListingGridSkeleton;
