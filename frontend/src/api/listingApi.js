import { apiClient } from "./apiClient";

// Fetch all active listings
export function getListings() {
  return apiClient("/listings", {
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
    requiresAuth: true,
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
