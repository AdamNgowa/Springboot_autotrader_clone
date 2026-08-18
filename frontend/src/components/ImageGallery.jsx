import { useState } from "react";
import { getImageUrl } from "../utils/getImageUrl";

function ImageGallery({ images = [], selectedImage, setSelectedImage, title }) {
  const [failedImageIds, setFailedImageIds] = useState([]);

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

  return (
    <>
      <div className="aspect-[16/9] w-full overflow-hidden rounded-xl bg-slate-200 shadow-sm">
        {displayedImage ? (
          <img
            src={getImageUrl(
              displayedImage.imageUrl ||
                displayedImage.url ||
                displayedImage.storageFilename,
            )}
            alt={title}
            className="h-full w-full object-cover transition-opacity duration-200"
            onError={() => handleImageError(displayedImage.id)}
          />
        ) : (
          <div className="flex h-full items-center justify-center text-slate-500">
            No image available
          </div>
        )}
      </div>

      {visibleImages.length > 1 && (
        <div className="flex gap-3 overflow-x-auto pb-2">
          {visibleImages.map((image) => (
            <button
              key={image.id}
              type="button"
              onClick={() => setSelectedImage(image)}
              className={`overflow-hidden rounded-lg border-2 transition-all duration-200
                hover:scale-105 hover:opacity-80
                ${
                  selectedImage?.id === image.id
                    ? "border-blue-600"
                    : "border-transparent"
                }`}
            >
              <img
                src={getImageUrl(
                  image.imageUrl || image.url || image.storageFilename,
                )}
                alt={title}
                className="h-20 w-28 object-cover"
                onError={() => handleImageError(image.id)}
              />
            </button>
          ))}
        </div>
      )}
    </>
  );
}

export default ImageGallery;
