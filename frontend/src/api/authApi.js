import { apiClient } from "./apiClient";

export async function login(credentials) {
  return apiClient("/auth/login", {
    method: "POST",
    requiresAuth: false,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });
}
