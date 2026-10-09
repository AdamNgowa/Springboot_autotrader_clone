import { useEffect, useRef, useState } from "react";
import {
  deleteImage,
  reorderImages,
  setPrimaryImage,
  uploadImage,
} from "../api/imageApi";
import { getImageUrl } from "../utils/getImageUrl";
import ManagedImageCard from "./ManagedImageCard";
import Notice from "./Notice";
import PendingImageCard from "./PendingImageCard";

function ImageManager({
  listingId,
  existingImages = [],
  disabled = false,
  onImagesChange,
}) {
  const [images, setImages] = useState(existingImages);
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploadProgress, setUploadProgress] = useState({});
  const [uploading, setUploading] = useState(false);
  const [deletingImageId, setDeletingImageId] = useState(null);
  const [primaryImageId, setPrimaryImageId] = useState(null);
  const [failedImageIds, setFailedImageIds] = useState([]);
  const [error, setError] = useState("");

  // Always holds the latest queue so the unmount cleanup can revoke its previews.
  const selectedFilesRef = useRef(selectedFiles);

  // ==========================================
  // SYNC EXISTING IMAGES
  // ==========================================

  useEffect(() => {
    setImages(existingImages);

    const primary = existingImages.find(
      (image) => image.primaryImage || image.isPrimary,
    );

    setPrimaryImageId(primary?.id ?? null);
  }, [existingImages]);

  // ==========================================
  // FILE SELECTION
  // ==========================================

  function handleFileChange(event) {
    const files = Array.from(event.target.files || []);

    if (files.length === 0) {
      return;
    }

    setError("");

    const newFiles = files.map((file) => ({
      id: crypto.randomUUID(),
      file,
      previewUrl: URL.createObjectURL(file),
    }));

    setSelectedFiles((current) => [...current, ...newFiles]);

    // Allow selecting the same file again later.
    event.target.value = "";
  }

  // ==========================================
  // REMOVE LOCAL PREVIEW
  // ==========================================

  function removeSelectedFile(fileId) {
    setSelectedFiles((current) => {
      const fileToRemove = current.find((item) => item.id === fileId);

      if (fileToRemove) {
        URL.revokeObjectURL(fileToRemove.previewUrl);
      }

      return current.filter((item) => item.id !== fileId);
    });

    setUploadProgress((current) => {
      const updated = { ...current };
      delete updated[fileId];
      return updated;
    });
  }

  // ==========================================
  // UPLOAD SELECTED FILES
  // ==========================================

  async function handleUpload() {
    if (!listingId) {
      setError("The listing must be saved before images can be uploaded.");
      return;
    }

    if (selectedFiles.length === 0) {
      return;
    }

    try {
      setUploading(true);
      setError("");

      const uploadedImages = [];

      for (const selectedFile of selectedFiles) {
        setUploadProgress((current) => ({
          ...current,
          [selectedFile.id]: 0,
        }));

        try {
          await uploadImage(listingId, selectedFile.file, (percentage) => {
            setUploadProgress((current) => ({
              ...current,
              [selectedFile.id]: percentage,
            }));
          });

          setUploadProgress((current) => ({
            ...current,
            [selectedFile.id]: 100,
          }));

          uploadedImages.push(selectedFile);
        } catch (error) {
          setError(
            error.message || `Failed to upload ${selectedFile.file.name}`,
          );
        }
      }

      // Remove successfully uploaded files from the local queue.
      setSelectedFiles((current) => {
        const successfulIds = new Set(uploadedImages.map((item) => item.id));

        current.forEach((item) => {
          if (successfulIds.has(item.id)) {
            URL.revokeObjectURL(item.previewUrl);
          }
        });

        return current.filter((item) => !successfulIds.has(item.id));
      });

      // Refresh the listing images from the parent.
      onImagesChange?.();
    } finally {
      setUploading(false);
    }
  }

  // ==========================================
  // DELETE EXISTING IMAGE
  // ==========================================

  async function handleDeleteImage(imageId) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this image?",
    );

    if (!confirmed) {
      return;
    }

    const previousImages = images;
    const previousPrimaryImageId = primaryImageId;

    try {
      setDeletingImageId(imageId);
      setError("");

      setImages((current) =>
        current.filter((image) => String(image.id) !== String(imageId)),
      );

      if (String(primaryImageId) === String(imageId)) {
        setPrimaryImageId(null);
      }

      await deleteImage(listingId, imageId);

      onImagesChange?.(imageId);
    } catch (error) {
      setImages(previousImages);
      setPrimaryImageId(previousPrimaryImageId);

      setError(error.message || "Failed to delete image.");
    } finally {
      setDeletingImageId(null);
    }
  }

  // ==========================================
  // SET PRIMARY IMAGE
  // ==========================================

  async function handleSetPrimary(imageId) {
    try {
      setError("");

      await setPrimaryImage(listingId, imageId);

      setImages((current) =>
        current.map((image) => ({
          ...image,
          primaryImage: image.id === imageId,
        })),
      );

      setPrimaryImageId(imageId);

      onImagesChange?.();
    } catch (error) {
      setError(error.message || "Failed to change primary image.");
    }
  }

  // ==========================================
  // MOVE IMAGE
  // ==========================================

  async function moveImage(index, direction) {
    const newIndex = direction === "left" ? index - 1 : index + 1;

    if (newIndex < 0 || newIndex >= images.length) {
      return;
    }

    const reorderedImages = [...images];

    const [movedImage] = reorderedImages.splice(index, 1);

    reorderedImages.splice(newIndex, 0, movedImage);

    // Optimistically update UI.
    setImages(reorderedImages);

    try {
      setError("");

      const imageIds = reorderedImages.map((image) => image.id);

      await reorderImages(listingId, imageIds);

      onImagesChange?.();
    } catch (error) {
      // Restore the previous ordering if the API fails.
      setImages(images);

      setError(error.message || "Failed to reorder images.");
    }
  }

  // ==========================================
  // CLEAN UP LOCAL PREVIEWS (on unmount only)
  // ==========================================

  useEffect(() => {
    selectedFilesRef.current = selectedFiles;
  }, [selectedFiles]);

  useEffect(() => {
    return () => {
      selectedFilesRef.current.forEach((item) => {
        URL.revokeObjectURL(item.previewUrl);
      });
    };
  }, []);

  const visibleImages = images.filter(
    (image) => !failedImageIds.includes(image.id),
  );

  return (
    <section className="space-y-6 border border-slate-300 bg-white p-4 sm:p-6">
      <div>
        <h2 className="text-lg font-semibold text-slate-900">Images</h2>

        <p className="mt-1 text-sm text-slate-500">
          Upload vehicle images, choose a primary image, and control their
          display order.
        </p>
      </div>

      {error && <Notice variant="error">{error}</Notice>}

      {/* ========================================
          EXISTING IMAGES
      ======================================== */}

      {visibleImages.length > 0 && (
        <div>
          <h3 className="mb-3 font-medium text-slate-800">Current images</h3>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {visibleImages.map((image, index) => {
              const isPrimary =
                image.primaryImage ||
                image.isPrimary ||
                primaryImageId === image.id;

              return (
                <ManagedImageCard
                  key={image.id}
                  image={image}
                  imageUrl={getImageUrl(
                    image.url || image.imageUrl || image.storageFilename,
                  )}
                  isPrimary={isPrimary}
                  isFirst={index === 0}
                  isLast={index === visibleImages.length - 1}
                  busy={disabled || deletingImageId === image.id}
                  deleting={deletingImageId === image.id}
                  onMakePrimary={() => handleSetPrimary(image.id)}
                  onMove={(direction) => moveImage(index, direction)}
                  onDelete={() => handleDeleteImage(image.id)}
                  onError={() =>
                    setFailedImageIds((current) =>
                      current.includes(image.id)
                        ? current
                        : [...current, image.id],
                    )
                  }
                />
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================
          FILE SELECTOR
      ======================================== */}

      <div>
        <label
          htmlFor="image-upload"
          className="mb-1.5 block text-sm font-medium text-slate-800"
        >
          Add images
        </label>

        <input
          id="image-upload"
          type="file"
          multiple
          accept="image/png,image/jpeg,image/webp"
          onChange={handleFileChange}
          disabled={disabled || uploading}
          className="block w-full border border-slate-300 text-sm file:mr-3 file:cursor-pointer file:border-0 file:bg-slate-900 file:px-4 file:py-2.5 file:text-sm file:font-medium file:text-white hover:file:bg-blue-600"
        />

        <p className="mt-1 text-xs text-slate-500">
          JPEG, PNG and WEBP images are supported (max 5MB each).
        </p>
      </div>

      {/* ========================================
          LOCAL PREVIEWS
      ======================================== */}

      {selectedFiles.length > 0 && (
        <div>
          <h3 className="mb-3 font-medium text-slate-800">
            Images ready to upload
          </h3>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {selectedFiles.map((item) => (
              <PendingImageCard
                key={item.id}
                item={item}
                progress={uploadProgress[item.id] ?? 0}
                uploading={uploading}
                disabled={disabled}
                onRemove={() => removeSelectedFile(item.id)}
              />
            ))}
          </div>

          <button
            type="button"
            onClick={handleUpload}
            disabled={disabled || uploading || selectedFiles.length === 0}
            className="btn btn-primary mt-4"
          >
            {uploading
              ? "Uploading..."
              : `Upload ${selectedFiles.length} ${
                  selectedFiles.length === 1 ? "image" : "images"
                }`}
          </button>
        </div>
      )}
    </section>
  );
}

export default ImageManager;
