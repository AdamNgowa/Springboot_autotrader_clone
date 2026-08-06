import { apiClient } from "./apiClient";

export function login(credentials) {
  return apiClient("/auth/login", {
    method: "POST",
    requiresAuth: false,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });
}

export function register(user) {
  return apiClient("/auth/register", {
    method: "POST",
    requiresAuth: false,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(user),
  });
}
