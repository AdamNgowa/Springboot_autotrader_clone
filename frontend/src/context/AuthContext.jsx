import { createContext, useEffect, useState } from "react";
import { login as loginRequest } from "../api/authApi";
import { getToken, saveToken, removeToken } from "../auth/authStorage";

// 1. CONTEXT CONTAINER
// Creates the Context object that child components will subscribe to.
export const AuthContext = createContext(null);

/**
 * 2. AUTH PROVIDER COMPONENT
 * Wraps the application and manages the global authentication lifecycle.
 */
export function AuthProvider({ children }) {
  // STATE: Holds the active JWT string in React memory (null = logged out).
  const [token, setToken] = useState(null);

  /**
   * APP STARTUP EFFECT:
   * Restores state from localStorage when the app first loads or refreshes.
   * - Runs ONLY ONCE on mount due to the empty dependency array `[]`.
   */
  useEffect(() => {
    const storedToken = getToken();

    if (storedToken) {
      setToken(storedToken); // Restores user session if a saved token exists.
    }
  }, []);

  /**
   * LOGIN ACTION:
   * Handles user login by calling the backend API, persisting the JWT to storage,
   * and updating the global React state.
   *
   * @param {Object} credentials - User inputs (e.g., { username, password }).
   * @returns {Promise<Object>} The server response containing token and user details.
   */
  async function login(credentials) {
    // Send HTTP POST request to backend auth endpoint.
    const response = await loginRequest(credentials);

    // Save token to browser localStorage for session persistence across refreshes.
    saveToken(response.token);

    // Update React state to trigger immediate UI re-renders for authenticated views.
    setToken(response.token);

    return response;
  }

  /**
   * LOGOUT ACTION:
   * Clears the user's session from both persistent storage and React memory.
   */
  function logout() {
    removeToken(); // Erases token from localStorage.
    setToken(null); // Resets state to null, instantly marking user as unauthenticated.
  }

  /**
   * BROADCAST PAYLOAD:
   * Data and methods exposed to all child components listening to this context.
   */
  const value = {
    token,
    isAuthenticated: token !== null, // Derived boolean flag for easy route protection.
    login,
    logout,
  };

  /**
   * CONTEXT PROVIDER RENDER:
   * Distributes the `value` payload to all nested `{children}` components.
   */
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
