import { useEffect } from "react";
import { getImageUrl } from "../utils/getImageUrl";
import GalleryArrow from "./GalleryArrow";

function imageSource(image) {
  return getImageUrl(image.imageUrl || image.url || image.storageFilename);
}

// Full-screen viewer. The gallery owns the state; this only displays it.
function ImageLightbox({
  images,
  currentIndex,
  title,
  onStep,
  onSelect,
  onClose,
}) {
  const current = images[currentIndex];

  // Keyboard controls and page scroll lock, active only while the viewer is mounted.
  useEffect(() => {
    function handleKeyDown(event) {
      if (event.key === "Escape") onClose();
      if (event.key === "ArrowLeft") onStep(-1);
      if (event.key === "ArrowRight") onStep(1);
    }

    document.addEventListener("keydown", handleKeyDown);
    document.body.style.overflow = "hidden";

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      document.body.style.overflow = "";
    };
  }, [onClose, onStep]);

  if (!current) return null;

  return (
    // z-[70] sits above the filter sheet (60) and the floating messages button (50).
    <div
      role="dialog"
      aria-modal="true"
      aria-label={`${title} photos`}
      className="fixed inset-0 z-[70] flex flex-col bg-black/95"
    >
      {/* Top bar */}
      <div className="flex items-center justify-between p-3 text-white">
        <span className="text-sm">
          {currentIndex + 1} / {images.length}
        </span>

        <button
          type="button"
          onClick={onClose}
          aria-label="Close photo viewer"
          className="flex h-10 w-10 items-center justify-center border border-white/40 hover:bg-white hover:text-black"
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
              d="M6 6l12 12M18 6 6 18"
            />
          </svg>
        </button>
      </div>

      {/* Large image. min-h-0 lets the flex child shrink instead of overflowing. */}
      <div className="relative flex min-h-0 flex-1 items-center justify-center px-2">
        <img
          src={imageSource(current)}
          alt={title}
          className="max-h-full max-w-full object-contain"
        />

        {images.length > 1 && (
          <>
            <GalleryArrow
              direction="left"
              onClick={() => onStep(-1)}
              className="absolute left-2 top-1/2 -translate-y-1/2"
            />
            <GalleryArrow
              direction="right"
              onClick={() => onStep(1)}
              className="absolute right-2 top-1/2 -translate-y-1/2"
            />
          </>
        )}
      </div>

      {/* Thumbnail strip: shrink-0 keeps every thumbnail the same size and scrolls sideways. */}
      <div className="flex gap-2 overflow-x-auto p-3">
        {images.map((image, index) => (
          <button
            key={image.id}
            type="button"
            onClick={() => onSelect(image)}
            aria-label={`Show image ${index + 1}`}
            className={`h-16 w-24 shrink-0 border-2 ${
              index === currentIndex
                ? "border-blue-500"
                : "border-transparent opacity-70 hover:opacity-100"
            }`}
          >
            <img
              src={imageSource(image)}
              alt=""
              className="h-full w-full object-cover"
            />
          </button>
        ))}
      </div>
    </div>
  );
}

export default ImageLightbox;
