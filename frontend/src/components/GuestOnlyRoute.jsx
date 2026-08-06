import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function GuestOnlyRoute({ children }) {
  const { loading, isAuthenticated } = useAuth();

  // Wait until session restoration completes before deciding.
  if (loading) {
    return <p>Loading...</p>;
  }

  // Authenticated users should not access guest-only pages.
  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default GuestOnlyRoute;
