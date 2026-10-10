import { apiClient } from "./apiClient";

export async function getCurrentUser() {
  return apiClient("/users/me", {
    method: "GET",
    requiresAuth: true,
  });
}

// Updates first name, last name and phone number. Resolves with the updated user.
export function updateCurrentUser(data) {
  return apiClient("/users/me", {
    method: "PUT",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

// Resolves with null on success (204 No Content).
export function changePassword(data) {
  return apiClient("/users/me/password", {
    method: "PUT",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}