// Base URL for the backend server.
const BASE_URL = "http://localhost:8080";

// A generic wrapper function for making API requests.
// - endpoint: The specific URL path you want to reach (like "/auth/login").
// - options: An optional object where you pass extra details like HTTP method ("POST"), body data, or headers.
export async function apiClient(endpoint, options = {}) {
  // Check if a saved authentication token exists in local storage
  const token = localStorage.getItem("jwt");

  // Copy any custom headers passed in through the options object (e.g., Content-Type)
  const headers = {
    ...options.headers,
  };

  // If a token exists, add the Authorization header so the server knows who we are
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  // Make the network request by combining the base URL and endpoint, passing along options and updated headers
  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  // Convert the server's JSON response into a JavaScript object
  const data = await response.json();

  //Rich error handling to return both error message and error status
  if (!response.ok) {
    const error = new Error(data.message || "Request failed");
    error.status = response.status;

    throw error;
  }

  // Return the result back to the caller
  return data;
}
