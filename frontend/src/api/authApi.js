import { apiClient } from "./apiClient";

export async function login(credentials) {
  const data = await apiClient("/auth/login", {
    method: "POST",

    //Login is a publc endpoint
    requiresAuth: false,

    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  localStorage.setItem("jwt", data.token);

  return data;
}
