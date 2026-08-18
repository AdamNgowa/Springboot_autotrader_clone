import { useEffect, useState } from "react";
import {
  deleteImage,
  reorderImages,
  setPrimaryImage,
  uploadImage,
} from "../api/imageApi";
import { getImageUrl } from "../utils/getImageUrl";

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
  const [error, setError] = useState("");

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

    try {
      setDeletingImageId(imageId);
      setError("");

      await deleteImage(listingId, imageId);

      setImages((current) => current.filter((image) => image.id !== imageId));

      if (primaryImageId === imageId) {
        setPrimaryImageId(null);
      }

      onImagesChange?.();
    } catch (error) {
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
  // CLEAN UP LOCAL PREVIEWS
  // ==========================================

  useEffect(() => {
    return () => {
      selectedFiles.forEach((item) => {
        URL.revokeObjectURL(item.previewUrl);
      });
    };
  }, [selectedFiles]);

  return (
    <section className="space-y-6 rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">Images</h2>

        <p className="mt-1 text-sm text-slate-500">
          Upload vehicle images, choose a primary image, and control their
          display order.
        </p>
      </div>

      {error && (
        <div className="rounded-lg bg-red-100 p-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* ========================================
          EXISTING IMAGES
      ======================================== */}

      {images.length > 0 && (
        <div>
          <h3 className="mb-3 font-medium text-slate-800">Current images</h3>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3">
            {images.map((image, index) => {
              const imageUrl = getImageUrl(
                image.url || image.imageUrl || image.storageFilename,
              );

              const isPrimary =
                image.primaryImage ||
                image.isPrimary ||
                primaryImageId === image.id;

              return (
                <div
                  key={image.id}
                  className="overflow-hidden rounded-lg border border-slate-200 bg-slate-50"
                >
                  <div className="relative aspect-video">
                    <img
                      src={imageUrl}
                      alt={image.originalFilename || "Vehicle"}
                      className="h-full w-full object-cover"
                    />

                    {isPrimary && (
                      <span className="absolute left-2 top-2 rounded-full bg-blue-600 px-3 py-1 text-xs font-semibold text-white">
                        Primary
                      </span>
                    )}
                  </div>

                  <div className="space-y-3 p-3">
                    <p className="truncate text-sm text-slate-600">
                      {image.originalFilename || "Vehicle image"}
                    </p>

                    <div className="flex flex-wrap gap-2">
                      {!isPrimary && (
                        <button
                          type="button"
                          disabled={disabled || deletingImageId === image.id}
                          onClick={() => handleSetPrimary(image.id)}
                          className="rounded-md bg-blue-600 px-3 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          Make Primary
                        </button>
                      )}

                      <button
                        type="button"
                        disabled={disabled || deletingImageId === image.id}
                        onClick={() => moveImage(index, "left")}
                        className="rounded-md border border-slate-300 px-3 py-2 text-sm hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        ←
                      </button>

                      <button
                        type="button"
                        disabled={disabled || deletingImageId === image.id}
                        onClick={() => moveImage(index, "right")}
                        className="rounded-md border border-slate-300 px-3 py-2 text-sm hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        →
                      </button>

                      <button
                        type="button"
                        disabled={disabled || deletingImageId === image.id}
                        onClick={() => handleDeleteImage(image.id)}
                        className="rounded-md bg-red-600 px-3 py-2 text-sm font-medium text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        {deletingImageId === image.id
                          ? "Deleting..."
                          : "Delete"}
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================
          FILE SELECTOR
      ======================================== */}

      <div>
        <label className="mb-2 block font-medium text-slate-800">
          Add images
        </label>

        <input
          type="file"
          multiple
          accept="image/png,image/jpeg,image/webp"
          onChange={handleFileChange}
          disabled={disabled || uploading}
          className="block w-full rounded-md border border-slate-300 p-2 text-sm"
        />

        <p className="mt-1 text-sm text-slate-500">
          JPEG, PNG and WEBP images are supported.
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

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3">
            {selectedFiles.map((item) => {
              const progress = uploadProgress[item.id] ?? 0;

              return (
                <div
                  key={item.id}
                  className="overflow-hidden rounded-lg border border-slate-200"
                >
                  <div className="aspect-video">
                    <img
                      src={item.previewUrl}
                      alt={item.file.name}
                      className="h-full w-full object-cover"
                    />
                  </div>

                  <div className="space-y-3 p-3">
                    <p className="truncate text-sm font-medium">
                      {item.file.name}
                    </p>

                    {/* Upload progress */}
                    {uploading && (
                      <div>
                        <div className="mb-1 flex justify-between text-xs text-slate-500">
                          <span>Uploading...</span>
                          <span>{progress}%</span>
                        </div>

                        <div className="h-2 overflow-hidden rounded-full bg-slate-200">
                          <div
                            className="h-full bg-blue-600 transition-all duration-200"
                            style={{
                              width: `${progress}%`,
                            }}
                          />
                        </div>
                      </div>
                    )}

                    {!uploading && (
                      <button
                        type="button"
                        disabled={disabled}
                        onClick={() => removeSelectedFile(item.id)}
                        className="rounded-md bg-red-600 px-3 py-2 text-sm font-medium text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        Remove
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          <button
            type="button"
            onClick={handleUpload}
            disabled={disabled || uploading || selectedFiles.length === 0}
            className="mt-4 rounded-lg bg-blue-600 px-5 py-3 font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
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
