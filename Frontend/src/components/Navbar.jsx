import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="bg-slate-900 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">

        <Link
          to="/dashboard"
          className="text-xl font-bold tracking-wide"
        >
          Uptime Monitor
        </Link>

        <div className="flex items-center gap-6">

          <Link
            to="/dashboard"
            className="text-sm text-slate-300 hover:text-white"
          >
            Dashboard
          </Link>

          <Link
            to="/status"
            className="text-sm text-slate-300 hover:text-white"
          >
            Public Status
          </Link>

          <div className="hidden md:block text-right">
            <p className="text-sm font-medium">
              {user?.email}
            </p>

            <p className="text-xs text-slate-400">
              {user?.role}
            </p>
          </div>

          <button
            onClick={handleLogout}
            className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded-lg text-sm font-medium transition"
          >
            Logout
          </button>

        </div>
      </div>
    </nav>
  );
}