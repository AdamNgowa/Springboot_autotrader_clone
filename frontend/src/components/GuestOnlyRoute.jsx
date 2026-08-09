import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function GuestOnlyRoute({ children }) {
  const { loading, isAuthenticated } = useAuth();
  const location = useLocation();

  // Wait until session restoration completes before deciding.
  if (loading) {
    return <p>Loading...</p>;
  }

  // Authenticated users should not remain on guest-only pages.
  // If they were originally trying to access a protected route,
  // return them there after authentication.
  if (isAuthenticated) {
    const redirectTo = location.state?.from?.pathname || "/";

    return <Navigate to={redirectTo} replace />;
  }

  return children;
}

export default GuestOnlyRoute;
