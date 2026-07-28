import { apiClient } from "./apiClient";
export async function getCurrentUser() {
  return apiClient("/users/me", {
    method: "GET",
    requiresAuth: true,
  });
}
