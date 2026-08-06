import { apiClient } from "./apiClient";

// Fetch all active listings
export function getListings(filters = {}) {
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== "" && value !== null && value !== undefined) {
      params.append(key, value);
    }
  });

  const queryString = params.toString();

  console.log(`/listings${queryString ? `?${queryString}` : ""}`);

  return apiClient(`/listings${queryString ? `?${queryString}` : ""}`, {
    method: "GET",
  });
}

// Fetch listings owned by the authenticated user
export function getMyListings() {
  return apiClient("/listings/me", {
    method: "GET",
    requiresAuth: true,
  });
}

// Fetch one listing
export function getListing(id) {
  return apiClient(`/listings/${id}`, {
    method: "GET",
  });
}

// Create a listing
export function createListing(data) {
  return apiClient("/listings", {
    method: "POST",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

// Update a listing
export function updateListing(id, data) {
  return apiClient(`/listings/${id}`, {
    method: "PUT",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

// Delete a listing
export function deleteListing(id) {
  return apiClient(`/listings/${id}`, {
    method: "DELETE",
    requiresAuth: true,
  });
}
