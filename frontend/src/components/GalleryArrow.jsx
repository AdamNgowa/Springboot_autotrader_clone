// A square previous/next button shared by the gallery and the lightbox.
function GalleryArrow({ direction, onClick, className = "" }) {
  const isLeft = direction === "left";

  return (
    <button
      type="button"
      onClick={onClick}
      aria-label={isLeft ? "Previous image" : "Next image"}
      className={`flex h-10 w-10 items-center justify-center border border-slate-300 bg-white text-slate-900 transition-colors hover:border-blue-600 hover:bg-blue-600 hover:text-white ${className}`}
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        className="h-5 w-5"
        aria-hidden="true"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d={
            isLeft ? "m15.75 19.5-7.5-7.5 7.5-7.5" : "m8.25 4.5 7.5 7.5-7.5 7.5"
          }
        />
      </svg>
    </button>
  );
}

export default GalleryArrow;
