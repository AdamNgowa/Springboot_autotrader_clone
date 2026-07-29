import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

//We pass components we want protected into this file
//Those components are passed into this component through children
//Then we return them
//return chilren; - simply means "Render whatever was placed inside me."
function ProtectedRoute({ children }) {
  const { user, loading, isAuthenticated } = useAuth();

  console.log("ProtectedRoute");
  console.log("User:", user);
  console.log("Authenticated:", isAuthenticated);

  //Don't immideatley redirect but instead wait for isAuthenticated to become true
  if (loading) {
    return <p>Loading...</p>;
  }

  //If condition results to true, then don't render this page
  //Instead send the browser to "/login"
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default ProtectedRoute;
