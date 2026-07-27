import { apiClient } from "./apiClient";

export async function login(credentials) {
  const data = await apiClient("/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  console.log("auth api: ", data);
  return data;
}
