import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

//We pass components we want protected into this file
//Those components are passed into this component through children
//Then we return them
//return chilren; - simply means "Render whatever was placed inside me."
function ProtectedRoute({ children }) {
  const { loading, isAuthenticated } = useAuth();

  //Don't immideatley redirect but instead wait for isAuthenticated to become true
  if (loading) {
    return <p>Loading...</p>;
  }

  // Redirect unauthenticated users to login.
  // 'replace' overwrites the history entry so the browser's Back button
  // takes them to their previous page instead of getting trapped in a redirect loop.
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default ProtectedRoute;
