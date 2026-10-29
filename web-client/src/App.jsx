import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navigation from './components/Navigation';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CustomerHomePage from './pages/CustomerHomePage';
import CustomerBookingsPage from './pages/CustomerBookingsPage';
import CustomerProfilePage from './pages/CustomerProfilePage';
import ProviderDashboardPage from './pages/ProviderDashboardPage';

function ProtectedRoute({ children, allowedRoles }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] flex items-center justify-center">
        <div className="w-8 h-8 rounded-full border-2 border-[#00D2FF] border-t-transparent animate-spin" />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={user.role === 'provider' ? '/provider' : '/'} replace />;
  }

  return (
    <>
      <Navigation />
      <main className="min-h-screen">
        {children}
      </main>
    </>
  );
}

function PublicRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] flex items-center justify-center">
        <div className="w-8 h-8 rounded-full border-2 border-[#00D2FF] border-t-transparent animate-spin" />
      </div>
    );
  }

  if (user) {
    return <Navigate to={user.role === 'provider' ? '/provider' : '/'} replace />;
  }

  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public Auth Routes */}
          <Route
            path="/login"
            element={
              <PublicRoute>
                <LoginPage />
              </PublicRoute>
            }
          />
          <Route
            path="/register"
            element={
              <PublicRoute>
                <RegisterPage />
              </PublicRoute>
            }
          />

          {/* Customer Routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute allowedRoles={['customer']}>
                <CustomerHomePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bookings"
            element={
              <ProtectedRoute allowedRoles={['customer']}>
                <CustomerBookingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <CustomerProfilePage />
              </ProtectedRoute>
            }
          />

          {/* Provider Routes */}
          <Route
            path="/provider"
            element={
              <ProtectedRoute allowedRoles={['provider']}>
                <ProviderDashboardPage />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
