import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";

export default function StatusPage() {
  const [statuses, setStatuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadStatuses = async () => {
      try {
        const response = await api.get("/status");

        setStatuses(
          Array.isArray(response.data)
            ? response.data
            : []
        );
      } catch (err) {
        console.error(err);
        setError("Unable to load system status");
      } finally {
        setLoading(false);
      }
    };

    loadStatuses();
  }, []);

  const operational =
    statuses.length > 0 &&
    statuses.every((s) => s.isUp);

  return (
    <div className="min-h-screen bg-slate-100">

      <header className="bg-slate-900 text-white">
        <div className="max-w-5xl mx-auto px-6 py-5 flex justify-between items-center">

          <Link
            to="/"
            className="text-xl font-bold"
          >
            Uptime Monitor
          </Link>

          <Link
            to="/login"
            className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded-lg text-sm font-medium"
          >
            Dashboard Login
          </Link>

        </div>
      </header>

      <main className="max-w-4xl mx-auto px-6 py-12">

        <div className="text-center mb-10">

          <h1 className="text-4xl font-bold text-slate-900">
            System Status
          </h1>

          <p className="text-slate-500 mt-2">
            Current status of our monitored services
          </p>

        </div>

        {!loading &&
          !error &&
          statuses.length > 0 && (
            <div
              className={`rounded-xl p-5 mb-8 border ${
                operational
                  ? "bg-green-50 border-green-200"
                  : "bg-red-50 border-red-200"
              }`}
            >

              <div className="flex items-center gap-3">

                <div
                  className={`w-3 h-3 rounded-full ${
                    operational
                      ? "bg-green-500"
                      : "bg-red-500"
                  }`}
                />

                <div>
                  <p
                    className={`font-bold ${
                      operational
                        ? "text-green-800"
                        : "text-red-800"
                    }`}
                  >
                    {operational
                      ? "All systems operational"
                      : "Some systems are experiencing issues"}
                  </p>

                  <p className="text-sm text-slate-500 mt-1">
                    Live monitoring status
                  </p>
                </div>

              </div>

            </div>
          )}

        {loading && (
          <div className="bg-white rounded-xl p-10 text-center shadow-sm">
            Loading status...
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-5 text-center">
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          statuses.length === 0 && (
            <div className="bg-white rounded-xl shadow-sm p-10 text-center">

              <p className="text-lg font-semibold text-slate-700">
                No public monitors
              </p>

              <p className="text-slate-500 mt-2">
                There are currently no services available
                on the public status page.
              </p>

            </div>
          )}

        <div className="space-y-4">

          {statuses.map((status) => (

            <div
              key={status.id}
              className="bg-white rounded-xl shadow-sm border border-slate-200 p-6"
            >

              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">

                <div>

                  <h2 className="font-bold text-lg text-slate-900">
                    {status.name}
                  </h2>

                  <p className="text-sm text-slate-500 mt-1">
                    Uptime over the last 24 hours
                  </p>

                </div>

                <div className="flex items-center gap-5">

                  <span className="text-sm font-semibold text-slate-600">
                    {status.uptimePercentage ?? 0}%
                  </span>

                  <span
                    className={`px-3 py-1.5 rounded-full text-xs font-bold ${
                      status.isUp
                        ? "bg-green-100 text-green-700"
                        : "bg-red-100 text-red-700"
                    }`}
                  >
                    {status.isUp
                      ? "Operational"
                      : "Down"}
                  </span>

                </div>

              </div>

            </div>

          ))}

        </div>

        <p className="text-center text-xs text-slate-400 mt-10">
          Status automatically reflects the latest health checks.
        </p>

      </main>
    </div>
  );
}