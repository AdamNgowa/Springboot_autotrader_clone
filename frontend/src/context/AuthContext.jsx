import { createContext, useEffect, useState } from "react";
import { login as loginRequest } from "../api/authApi";
import { getToken, saveToken, removeToken } from "../auth/authStorage";
import { getCurrentUser } from "../api/userApi";

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

  // STATE: Holds the current user object once fetched from the API.
  // null = we don't have a confirmed user yet (logged out, OR still checking).
  // This is the "source of truth" that isAuthenticated below is derived from.
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  /**
   * APP STARTUP EFFECT:
   * Restores state from localStorage when the app first loads or refreshes.
   * - Runs ONLY ONCE on mount due to the empty dependency array `[]`.
   */
  useEffect(() => {
    async function restoreSession() {
      console.log("restoreSession started");
      const storedToken = getToken();

      if (!storedToken) {
        setLoading(false);
        return;
      }

      setToken(storedToken);

      try {
        const currentUser = await getCurrentUser();
        setUser(currentUser);
        setLoading(false);
      } catch {
        removeToken();
        setToken(null);
        setUser(null);
        setLoading(false);
      }
    }

    restoreSession();
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

    const currentUser = await getCurrentUser();
    setUser(currentUser); // user goes from null -> object, so isAuthenticated flips true

    return response;
  }

  /**
   * LOGOUT ACTION:
   * Clears the user's session from both persistent storage and React memory.
   */
  function logout() {
    removeToken(); // Erases token from localStorage.
    setToken(null); // Resets state to null, instantly marking user as unauthenticated.
    setUser(null); // user goes back to null, so isAuthenticated flips false
  }

  /**
   * BROADCAST PAYLOAD:
   * Data and methods exposed to all child components listening to this context.
   */
  const value = {
    token,
    user,

    // DERIVED VALUE (not its own useState):
    // Strict inequality check `!==` asks "is user NOT equal to null?"
    //   - user is null        -> user !== null is false -> isAuthenticated: false
    //   - user is an object   -> user !== null is true  -> isAuthenticated: true
    // Deriving it from `user` (instead of tracking a separate state variable)
    // guarantees isAuthenticated can never drift out of sync with the actual user data.
    isAuthenticated: user !== null,
    loading,
    login,
    logout,
  };

  /**
   * CONTEXT PROVIDER RENDER:
   * Distributes the `value` payload to all nested `{children}` components.
   */
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
