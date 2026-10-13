import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/axios";
import Navbar from "../components/Navbar";

export default function MonitorDetails() {
  const { id } = useParams();

  const [monitor, setMonitor] = useState(null);
  const [checks, setChecks] = useState([]);
  const [uptime, setUptime] = useState(null);

  const [hours, setHours] = useState(24);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        monitorResponse,
        checksResponse,
        uptimeResponse,
      ] = await Promise.all([
        api.get(`/monitor/${id}`),
        api.get(`/monitor/${id}/checks`),
        api.get(
          `/monitor/${id}/uptime?hours=${hours}`
        ),
      ]);

      setMonitor(monitorResponse.data);

      setChecks(
        Array.isArray(checksResponse.data)
          ? checksResponse.data
          : []
      );

      setUptime(uptimeResponse.data);
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to load monitor details"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id, hours]);

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-100">
        <Navbar />

        <div className="max-w-7xl mx-auto p-10 text-center text-slate-500">
          Loading monitor...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-slate-100">
        <Navbar />

        <div className="max-w-4xl mx-auto p-6">

          <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-5">
            {error}
          </div>

          <Link
            to="/dashboard"
            className="inline-block mt-5 text-blue-600 hover:underline"
          >
            ← Back to Dashboard
          </Link>

        </div>
      </div>
    );
  }

  if (!monitor) {
    return null;
  }

  return (
    <div className="min-h-screen bg-slate-100">

      <Navbar />

      <main className="max-w-7xl mx-auto px-6 py-8">

        <Link
          to="/dashboard"
          className="text-blue-600 hover:underline text-sm"
        >
          ← Back to Dashboard
        </Link>

        <div className="mt-5 flex flex-col md:flex-row md:items-center justify-between gap-4">

          <div>
            <div className="flex items-center gap-3">

              <h1 className="text-3xl font-bold text-slate-900">
                {monitor.name}
              </h1>

              <span
                className={`px-3 py-1 rounded-full text-xs font-semibold ${
                  monitor.isActive
                    ? "bg-green-100 text-green-700"
                    : "bg-slate-200 text-slate-600"
                }`}
              >
                {monitor.isActive
                  ? "ACTIVE"
                  : "PAUSED"}
              </span>

            </div>

            <p className="text-slate-500 mt-2 break-all">
              {monitor.url}
            </p>
          </div>

          <select
            value={hours}
            onChange={(e) =>
              setHours(Number(e.target.value))
            }
            className="border border-slate-300 bg-white rounded-lg px-4 py-2"
          >
            <option value={1}>Last 1 hour</option>
            <option value={6}>Last 6 hours</option>
            <option value={12}>Last 12 hours</option>
            <option value={24}>Last 24 hours</option>
            <option value={48}>Last 48 hours</option>
            <option value={168}>Last 7 days</option>
          </select>

        </div>

        {/* Statistics */}

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5 mt-8">

          <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">

            <p className="text-sm text-slate-500">
              Uptime
            </p>

            <p className="text-4xl font-bold text-green-600 mt-2">
              {uptime !== null
                ? `${Number(uptime).toFixed(2)}%`
                : "N/A"}
            </p>

            <p className="text-xs text-slate-400 mt-2">
              Last {hours} hours
            </p>

          </div>

          <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">

            <p className="text-sm text-slate-500">
              Consecutive Failures
            </p>

            <p
              className={`text-4xl font-bold mt-2 ${
                monitor.consecutiveFailures >= 3
                  ? "text-red-600"
                  : "text-slate-900"
              }`}
            >
              {monitor.consecutiveFailures ?? 0}
            </p>

          </div>

          <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">

            <p className="text-sm text-slate-500">
              Visibility
            </p>

            <p className="text-2xl font-bold mt-3">
              {monitor.isPublic
                ? "Public"
                : "Private"}
            </p>

          </div>

        </div>

        {/* Checks */}

        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 mt-8 overflow-hidden">

          <div className="px-6 py-5 border-b border-slate-200">
            <h2 className="text-xl font-bold">
              Recent Health Checks
            </h2>
          </div>

          {checks.length === 0 ? (
            <div className="p-10 text-center text-slate-500">
              No health checks available yet.
            </div>
          ) : (
            <div className="overflow-x-auto">

              <table className="w-full">

                <thead className="bg-slate-50">

                  <tr>
                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Status
                    </th>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      HTTP
                    </th>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Response Time
                    </th>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Checked At
                    </th>
                  </tr>

                </thead>

                <tbody className="divide-y divide-slate-200">

                  {checks.map((check) => (

                    <tr
                      key={check.id}
                      className="hover:bg-slate-50"
                    >

                      <td className="px-6 py-4">

                        <span
                          className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                            check.isUp
                              ? "bg-green-100 text-green-700"
                              : "bg-red-100 text-red-700"
                          }`}
                        >
                          {check.isUp
                            ? "UP"
                            : "DOWN"}
                        </span>

                      </td>

                      <td className="px-6 py-4 text-sm">
                        {check.statusCode || "-"}
                      </td>

                      <td className="px-6 py-4 text-sm">
                        {check.responseTimeMs !==
                        undefined
                          ? `${check.responseTimeMs} ms`
                          : "-"}
                      </td>

                      <td className="px-6 py-4 text-sm text-slate-500">
                        {check.checkedAt
                          ? new Date(
                              check.checkedAt
                            ).toLocaleString()
                          : "-"}
                      </td>

                    </tr>

                  ))}

                </tbody>

              </table>

            </div>
          )}

        </div>

      </main>
    </div>
  );
}