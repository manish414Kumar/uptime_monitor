import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";

import ProtectedRoute from "./components/ProtectedRoute";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import MonitorDetails from "./pages/MonitorDetails";
import StatusPage from "./pages/StatusPage";

export default function App() {
  return (
    <AuthProvider>

      <BrowserRouter>

        <Routes>

          {/* Public */}

          <Route
            path="/"
            element={<StatusPage />}
          />

          <Route
            path="/status"
            element={<StatusPage />}
          />

          <Route
            path="/login"
            element={<Login />}
          />

          <Route
            path="/register"
            element={<Register />}
          />

          {/* Protected */}

          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/monitor/:id"
            element={
              <ProtectedRoute>
                <MonitorDetails />
              </ProtectedRoute>
            }
          />

        </Routes>

      </BrowserRouter>

    </AuthProvider>
  );
}