import { apiClient } from "./apiClient";

/**
 * Add a vehicle listing to the authenticated user's favorites.
 *
 * @param {number} listingId - ID of the listing to favorite.
 * @returns {Promise<null>}
 */
export async function addFavorite(listingId) {
  return apiClient(`/favorites/${listingId}`, {
    method: "POST",
    requiresAuth: true,
  });
}

/**
 * Remove a vehicle listing from the authenticated user's favorites.
 *
 * @param {number} listingId - ID of the listing to remove.
 * @returns {Promise<null>}
 */
export async function removeFavorite(listingId) {
  return apiClient(`/favorites/${listingId}`, {
    method: "DELETE",
    requiresAuth: true,
  });
}

/**
 * Retrieve the authenticated user's favorite listings.
 *
 * @returns {Promise<Array>}
 */
export async function getFavorites() {
  return apiClient("/favorites", {
    method: "GET",
    requiresAuth: true,
  });
}

/**
 * Check whether a listing is currently favorited
 * by the authenticated user.
 *
 * @param {number} listingId - ID of the listing.
 * @returns {Promise<{favorite: boolean}>}
 */
export async function getFavoriteStatus(listingId) {
  return apiClient(`/favorites/${listingId}/status`, {
    method: "GET",
    requiresAuth: true,
  });
}
