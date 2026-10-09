import { useEffect, useRef, useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout, isAuthenticated, loading } = useAuth();

  // Two independent pieces of UI state: the mobile menu and the desktop dropdown.
  const [menuOpen, setMenuOpen] = useState(false);
  const [userOpen, setUserOpen] = useState(false);

  // Points at the dropdown wrapper so we can tell inside-clicks from outside-clicks.
  const userMenuRef = useRef(null);

  const initial = user?.firstName?.[0]?.toUpperCase() ?? "?";

  // The navbar lives outside <Routes>, so it never unmounts on navigation.
  // Close both menus whenever the route changes.
  useEffect(() => {
    setMenuOpen(false);
    setUserOpen(false);
  }, [location.pathname]);

  // Close the dropdown on outside click or Escape. Listeners exist only while it is open.
  useEffect(() => {
    if (!userOpen) return;

    function handlePointerDown(event) {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setUserOpen(false);
      }
    }

    function handleKeyDown(event) {
      if (event.key === "Escape") {
        setUserOpen(false);
      }
    }

    document.addEventListener("pointerdown", handlePointerDown);
    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("pointerdown", handlePointerDown);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [userOpen]);

  function handleLogout() {
    setUserOpen(false);
    setMenuOpen(false);
    logout();
    navigate("/");
  }

  // Desktop link: underline bar on the active route.
  function desktopLink({ isActive }) {
    return `flex h-14 items-center border-b-2 px-3 text-sm transition-colors hover:bg-slate-100 ${
      isActive
        ? "border-blue-600 font-medium text-slate-900"
        : "border-transparent text-slate-600 hover:text-slate-900"
    }`;
  }

  // Mobile link: full-width row with a left bar on the active route.
  function mobileLink({ isActive }) {
    return `block border-t border-slate-200 border-l-4 px-4 py-3 text-sm hover:bg-slate-100 ${
      isActive
        ? "border-l-blue-600 font-medium"
        : "border-l-transparent text-slate-700"
    }`;
  }

  const dropdownItem =
    "block px-4 py-2.5 text-left text-sm text-slate-900 hover:bg-slate-100";

  return (
    <header className="sticky top-0 z-40 border-b border-slate-300 bg-white">
      <nav className="mx-auto flex h-14 max-w-7xl items-center gap-2 px-4">
        {/* Logo */}
        <Link
          to="/"
          className="mr-4 text-xl font-bold tracking-tight text-blue-600"
        >
          AutoTrader
        </Link>

        {/* Desktop links */}
        <div className="hidden items-center md:flex">
          <NavLink to="/" end className={desktopLink}>
            Browse
          </NavLink>

          {isAuthenticated && (
            <NavLink to="/listings/new" className={desktopLink}>
              Sell vehicle
            </NavLink>
          )}
        </div>

        {/* Desktop right side */}
        <div className="ml-auto hidden items-center gap-2 md:flex">
          {loading ? (
            <span className="text-sm text-slate-500">Loading...</span>
          ) : isAuthenticated ? (
            <div className="relative" ref={userMenuRef}>
              <button
                type="button"
                onClick={() => setUserOpen((open) => !open)}
                aria-haspopup="menu"
                aria-expanded={userOpen}
                className="flex h-9 items-center gap-2 border border-slate-300 bg-white px-2.5 text-sm hover:bg-slate-100"
              >
                <span className="flex h-6 w-6 items-center justify-center bg-blue-600 text-xs font-medium text-white">
                  {initial}
                </span>
                {user?.firstName}
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  className={`h-4 w-4 transition-transform ${
                    userOpen ? "rotate-180" : ""
                  }`}
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="m19.5 8.25-7.5 7.5-7.5-7.5"
                  />
                </svg>
              </button>

              {userOpen && (
                <div
                  role="menu"
                  className="absolute right-0 top-full z-50 mt-1 w-52 border border-slate-300 bg-white"
                >
                  <Link
                    to="/my-listings"
                    role="menuitem"
                    className={dropdownItem}
                  >
                    My listings
                  </Link>
                  <Link
                    to="/favorites"
                    role="menuitem"
                    className={dropdownItem}
                  >
                    Favorites
                  </Link>
                  <Link
                    to="/conversations"
                    role="menuitem"
                    className={dropdownItem}
                  >
                    Messages
                  </Link>
                  <button
                    type="button"
                    role="menuitem"
                    onClick={handleLogout}
                    className={`${dropdownItem} w-full border-t border-slate-200 text-red-600 hover:bg-red-50`}
                  >
                    Log out
                  </button>
                </div>
              )}
            </div>
          ) : (
            <>
              <Link to="/login" className="btn btn-outline">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary">
                Register
              </Link>
            </>
          )}
        </div>

        {/* Mobile hamburger */}
        <button
          type="button"
          onClick={() => setMenuOpen((open) => !open)}
          aria-label={menuOpen ? "Close menu" : "Open menu"}
          aria-expanded={menuOpen}
          className="ml-auto flex h-9 w-9 items-center justify-center border border-slate-300 hover:bg-slate-100 md:hidden"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            className="h-5 w-5"
            aria-hidden="true"
          >
            {menuOpen ? (
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M6 6l12 12M18 6 6 18"
              />
            ) : (
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M4 7h16M4 12h16M4 17h16"
              />
            )}
          </svg>
        </button>
      </nav>

      {/* Mobile menu panel */}
      {menuOpen && (
        <div className="border-b border-slate-300 bg-white md:hidden">
          <NavLink to="/" end className={mobileLink}>
            Browse
          </NavLink>

          {isAuthenticated ? (
            <>
              <NavLink to="/listings/new" className={mobileLink}>
                Sell vehicle
              </NavLink>
              <NavLink to="/my-listings" className={mobileLink}>
                My listings
              </NavLink>
              <NavLink to="/favorites" className={mobileLink}>
                Favorites
              </NavLink>
              <NavLink to="/conversations" className={mobileLink}>
                Messages
              </NavLink>

              <div className="flex items-center justify-between border-t border-slate-200 px-4 py-3">
                <span className="text-sm text-slate-600">
                  Signed in as {user?.firstName}
                </span>
                <button
                  type="button"
                  onClick={handleLogout}
                  className="btn btn-danger"
                >
                  Log out
                </button>
              </div>
            </>
          ) : (
            <div className="flex gap-2 border-t border-slate-200 p-4">
              <Link to="/login" className="btn btn-outline flex-1">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary flex-1">
                Register
              </Link>
            </div>
          )}
        </div>
      )}
    </header>
  );
}

export default Navbar;
