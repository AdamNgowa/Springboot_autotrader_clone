import { createContext, useEffect, useState } from "react";
import {
  login as loginRequest,
  register as registerRequest,
} from "../api/authApi";
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
   * SESSION INITIALIZATION:
   * Completes the authentication lifecycle once a valid JWT has been received.
   *
   * @param {string} token - JWT returned by the backend.
   */

  async function initializeSession(token) {
    // Persist the JWT so the session survives browser refreshes.
    saveToken(token);

    // Update React state immediately.
    setToken(token);

    try {
      // Load the authenticated user's profile.
      const currentUser = await getCurrentUser();

      // Store the authenticated user in context.
      setUser(currentUser);
    } catch (error) {
      // The token cannot establish a valid authenticated session,
      // so roll back both persistent storage and React state.
      removeToken();
      setToken(null);
      setUser(null);

      throw error;
    }
  }

  async function login(credentials) {
    const response = await loginRequest(credentials);

    await initializeSession(response.token);

    return response;
  }

  async function register(userData) {
    const response = await registerRequest(userData);

    await initializeSession(response.token);

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
    register,
    logout,
  };

  /**
   * CONTEXT PROVIDER RENDER:
   * Distributes the `value` payload to all nested `{children}` components.
   */
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
