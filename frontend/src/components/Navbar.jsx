import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function Navbar() {
  const navigate = useNavigate();

  const { user, logout, isAuthenticated, loading } = useAuth();

  function handleLogout() {
    logout();
    navigate("/");
  }

  function navClass({ isActive }) {
    return isActive
      ? "font-semibold text-blue-600"
      : "text-gray-700 hover:text-blue-600";
  }

  return (
    <header className="border-b bg-white shadow-sm">
      <nav className="mx-auto flex max-w-6xl items-center justify-between p-4">
        {/* Logo */}
        <Link
          to="/"
          className="text-2xl font-bold text-blue-600 hover:text-blue-700"
        >
          AutoTrader
        </Link>

        {/* Navigation */}
        <div className="flex items-center gap-6">
          <NavLink to="/" className={navClass}>
            Home
          </NavLink>

          {isAuthenticated && (
            <>
              <NavLink to="/my-listings" className={navClass}>
                My Listings
              </NavLink>

              <NavLink to="/listings/new" className={navClass}>
                Sell Vehicle
              </NavLink>
            </>
          )}
        </div>

        {/* Right side */}
        <div className="flex items-center gap-3">
          {loading ? (
            <span className="text-gray-500">Loading...</span>
          ) : isAuthenticated ? (
            <>
              <span className="rounded-full bg-gray-100 px-3 py-2 text-sm">
                Hi, {user.firstName}
              </span>

              <button
                onClick={handleLogout}
                className="rounded-md bg-red-600 px-4 py-2 text-white transition hover:bg-red-700"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="rounded-md border border-blue-600 px-4 py-2 text-blue-600 transition hover:bg-blue-50"
              >
                Login
              </Link>

              <Link
                to="/register"
                className="rounded-md bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Navbar;
