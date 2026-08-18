import { apiClient } from "./apiClient";
import { getToken } from "../auth/authStorage";

const BASE_URL = "http://localhost:8080";

// ==========================================
// IMAGE UPLOAD WITH PROGRESS
// ==========================================

export function uploadImage(listingId, file, onProgress) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();

    xhr.open("POST", `${BASE_URL}/listings/${listingId}/images`);

    const token = getToken();

    if (token) {
      xhr.setRequestHeader("Authorization", `Bearer ${token}`);
    }

    xhr.upload.addEventListener("progress", (event) => {
      if (!event.lengthComputable) {
        return;
      }

      const percentage = Math.round((event.loaded / event.total) * 100);

      onProgress?.(percentage);
    });

    xhr.addEventListener("load", () => {
      let data = null;

      if (xhr.responseText) {
        try {
          data = JSON.parse(xhr.responseText);
        } catch {
          data = null;
        }
      }

      if (xhr.status >= 200 && xhr.status < 300) {
        resolve(data);
        return;
      }

      const error = new Error(data?.message || "Image upload failed");

      error.status = xhr.status;
      error.data = data;

      reject(error);
    });

    xhr.addEventListener("error", () => {
      reject(new Error("Network error while uploading image"));
    });

    const formData = new FormData();

    formData.append("file", file);

    xhr.send(formData);
  });
}

// ==========================================
// DELETE IMAGE
// ==========================================

export function deleteImage(listingId, imageId) {
  return apiClient(`/listings/${listingId}/images/${imageId}`, {
    method: "DELETE",
    requiresAuth: true,
  });
}

// ==========================================
// SET PRIMARY IMAGE
// ==========================================

export function setPrimaryImage(listingId, imageId) {
  return apiClient(`/listings/${listingId}/images/${imageId}/primary`, {
    method: "PATCH",
    requiresAuth: true,
  });
}

// ==========================================
// REORDER IMAGES
// ==========================================

export function reorderImages(listingId, imageIds) {
  return apiClient(`/listings/${listingId}/images/order`, {
    method: "PUT",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      imageIds,
    }),
  });
}
