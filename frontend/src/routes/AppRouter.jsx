import { BrowserRouter, Route, Routes } from "react-router-dom";
import HomePage from "../pages/HomePage";
import LoginPage from "../pages/LoginPage";
import DashboardPage from "../pages/DashboardPage";
import ProtectedRoute from "../components/ProtectedRoute";
import MyListingsPage from "../pages/MyListingsPage";
import CreateListingPage from "../pages/CreateListingPage";
import EditListingPage from "../pages/EditListingPage";
import ListingDetailsPage from "../pages/ListingDetailsPage";
import Navbar from "../components/Navbar";
import RegisterPage from "../pages/RegisterPage";
import GuestOnlyRoute from "../components/GuestOnlyRoute";
import FavoritesPage from "../pages/FavoritesPage";
import SellerProfilePage from "../pages/SellerProfilePage";

function AppRouter() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/listings/:id" element={<ListingDetailsPage />} />
        <Route path="/sellers/:id" element={<SellerProfilePage />} />
        <Route
          path="/login"
          element={
            <GuestOnlyRoute>
              <LoginPage />
            </GuestOnlyRoute>
          }
        />
        <Route
          path="/register"
          element={
            <GuestOnlyRoute>
              <RegisterPage />
            </GuestOnlyRoute>
          }
        />
        <Route
          path="/favorites"
          element={
            <ProtectedRoute>
              <FavoritesPage />
            </ProtectedRoute>
          }
        />{" "}
        {/* Protected routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              {" "}
              <DashboardPage />{" "}
            </ProtectedRoute>
          }
        />
        <Route
          path={`/listings/:id/edit`}
          element={
            <ProtectedRoute>
              {" "}
              <EditListingPage />{" "}
            </ProtectedRoute>
          }
        />
        <Route
          path="/my-listings"
          element={
            <ProtectedRoute>
              <MyListingsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/listings/new"
          element={
            <ProtectedRoute>
              <CreateListingPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;
