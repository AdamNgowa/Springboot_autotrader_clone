import { useState } from "react";
import { getImageUrl } from "../utils/getImageUrl";
import GalleryArrow from "./GalleryArrow";
import ImageLightbox from "./ImageLightbox";

// Number of thumbnail tiles. If there are more images, the last tile becomes "+N".
const THUMB_SLOTS = 5;

function imageSource(image) {
  return getImageUrl(image.imageUrl || image.url || image.storageFilename);
}

function ImageGallery({ images = [], selectedImage, setSelectedImage, title }) {
  const [failedImageIds, setFailedImageIds] = useState([]);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  const visibleImages = images.filter(
    (image) =>
      (image.imageUrl || image.url || image.storageFilename) &&
      !failedImageIds.includes(image.id),
  );

  const displayedImage = visibleImages.some(
    (image) => image.id === selectedImage?.id,
  )
    ? selectedImage
    : visibleImages[0] || null;

  const total = visibleImages.length;
  const currentIndex = displayedImage
    ? visibleImages.findIndex((image) => image.id === displayedImage.id)
    : -1;

  function handleImageError(imageId) {
    setFailedImageIds((current) =>
      current.includes(imageId) ? current : [...current, imageId],
    );

    if (selectedImage?.id === imageId) {
      setSelectedImage(
        visibleImages.find((image) => image.id !== imageId) || null,
      );
    }
  }

  // Moves to the previous (-1) or next (+1) image, wrapping at both ends.
  function step(direction) {
    if (total < 2) return;

    const nextIndex = (currentIndex + direction + total) % total;
    setSelectedImage(visibleImages[nextIndex]);
  }

  const hasOverflow = total > THUMB_SLOTS;
  const thumbnails = visibleImages.slice(0, THUMB_SLOTS);
  // The last tile covers itself and every image after it.
  const overflowCount = total - (THUMB_SLOTS - 1);

  return (
    <>
      {/* Main image */}
      <div className="relative aspect-[4/3] w-full overflow-hidden border border-slate-300 bg-slate-100 md:aspect-[16/10]">
        {displayedImage ? (
          <>
            <button
              type="button"
              onClick={() => setLightboxOpen(true)}
              aria-label="View larger"
              className="block h-full w-full cursor-zoom-in"
            >
              <img
                src={imageSource(displayedImage)}
                alt={title}
                className="h-full w-full object-cover"
                onError={() => handleImageError(displayedImage.id)}
              />
            </button>

            {total > 1 && (
              <>
                <GalleryArrow
                  direction="left"
                  onClick={() => step(-1)}
                  className="absolute left-2 top-1/2 -translate-y-1/2"
                />
                <GalleryArrow
                  direction="right"
                  onClick={() => step(1)}
                  className="absolute right-2 top-1/2 -translate-y-1/2"
                />

                <span className="absolute bottom-2 right-2 bg-black/70 px-2 py-1 text-xs text-white">
                  {currentIndex + 1} / {total}
                </span>
              </>
            )}
          </>
        ) : (
          <div className="flex h-full items-center justify-center text-slate-500">
            No image available
          </div>
        )}
      </div>

      {/* Thumbnails: a fixed 5-column grid of squares, so they can never squash. */}
      {total > 1 && (
        <div className="mt-2 grid grid-cols-5 gap-2">
          {thumbnails.map((image, index) => {
            const isOverflowTile = hasOverflow && index === THUMB_SLOTS - 1;
            const isSelected = image.id === displayedImage?.id;

            return (
              <button
                key={image.id}
                type="button"
                onClick={() =>
                  isOverflowTile
                    ? setLightboxOpen(true)
                    : setSelectedImage(image)
                }
                aria-label={
                  isOverflowTile
                    ? `View all ${total} images`
                    : `Show image ${index + 1}`
                }
                className={`relative aspect-square overflow-hidden border-2 transition-colors ${
                  isSelected
                    ? "border-blue-600"
                    : "border-transparent hover:border-slate-900"
                }`}
              >
                <img
                  src={imageSource(image)}
                  alt=""
                  className="h-full w-full object-cover"
                  onError={() => handleImageError(image.id)}
                />

                {isOverflowTile && (
                  <span className="absolute inset-0 flex items-center justify-center bg-black/60 text-lg font-medium text-white">
                    +{overflowCount}
                  </span>
                )}
              </button>
            );
          })}
        </div>
      )}

      {lightboxOpen && total > 0 && (
        <ImageLightbox
          images={visibleImages}
          currentIndex={Math.max(currentIndex, 0)}
          title={title}
          onStep={step}
          onSelect={setSelectedImage}
          onClose={() => setLightboxOpen(false)}
        />
      )}
    </>
  );
}

export default ImageGallery;
