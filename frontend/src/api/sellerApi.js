import { apiClient } from "./apiClient";

// Fetch a public seller profile
export function getSellerProfile(sellerId) {
  return apiClient(`/users/${sellerId}`, {
    method: "GET",
  });
}

// Fetch a seller's active listings
export function getSellerListings(sellerId, pagination = {}) {
  const params = new URLSearchParams();

  Object.entries(pagination).forEach(([key, value]) => {
    if (value !== "" && value !== null && value !== undefined) {
      params.append(key, value);
    }
  });

  const queryString = params.toString();

  return apiClient(
    `/users/${sellerId}/listings${queryString ? `?${queryString}` : ""}`,
    {
      method: "GET",
    }
  );
}