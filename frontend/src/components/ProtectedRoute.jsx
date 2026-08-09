import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

// We pass components we want protected into this file.
// Those components are passed into this component through children.
// Then we return them.
// return children; - simply means "Render whatever was placed inside me."

function ProtectedRoute({ children }) {
  const { loading, isAuthenticated } = useAuth();
  const location = useLocation();

  // Don't immediately redirect but instead wait for isAuthenticated to become true.
  if (loading) {
    return <p>Loading...</p>;
  }

  // Redirect unauthenticated users to login.
  // The current location is passed along so LoginPage
  // can return the user to the route they originally requested.
  //
  // 'replace' overwrites the history entry so the browser's Back button
  // takes them to their previous page instead of getting trapped in a redirect loop.
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
}

export default ProtectedRoute;
