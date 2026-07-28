import { getToken } from "../auth/authStorage";

// Base URL for the backend API endpoints.
const BASE_URL = "http://localhost:8080";

/**
 * Generic API client for making HTTP requests using the Fetch API.
 *
 * @param {string} endpoint - The API path (e.g., "/auth/login" or "/users").
 * @param {Object} [options={}] - Custom options extending standard fetch parameters.
 * @param {boolean} [options.requiresAuth=false] - Whether to attach the JWT Bearer token.
 * @returns {Promise<any>} The parsed JSON data or null if the response body is empty.
 */
export async function apiClient(endpoint, options = {}) {
  // Separate application-specific options (requiresAuth) from standard fetch options.
  const { requiresAuth = false, ...fetchOptions } = options;

  // Retrieve the JSON Web Token stored in the browser's LocalStorage.
  const token = getToken();

  // Create a headers object initialized with any custom headers passed by the caller.
  const headers = {
    ...fetchOptions.headers,
  };

  // Attach the Authorization header only if the route explicitly requires authentication and a token exists.
  if (requiresAuth && token) {
    headers.Authorization = `Bearer ${token}`;
  }

  // Execute the network request using the combined base URL, endpoint, options, and headers.
  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...fetchOptions,
    headers,
  });

  // Inspect the Content-Type header to safely handle response parsing.
  const contentType = response.headers.get("Content-Type");

  let data = null;
  // Parse as JSON only if the header confirms the server sent JSON data (prevents syntax errors on empty/HTML responses).
  if (contentType && contentType.includes("application/json")) {
    data = await response.json();
  }

  // If the HTTP status code is outside the 200–299 range, throw a custom error.
  if (!response.ok) {
    // Fall back to a generic message if no structured error message was returned in the response body.
    const error = new Error(data?.message || "Request failed");
    error.status = response.status;

    throw error;
  }

  // Return the parsed JSON response object (or null for empty responses like 204 No Content).
  return data;
}
