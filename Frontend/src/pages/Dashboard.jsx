import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import Navbar from "../components/Navbar";

export default function Dashboard() {
  const [monitors, setMonitors] = useState([]);

  const [form, setForm] = useState({
    name: "",
    url: "",
    isPublic: true,
    webHookUrl: "",
    alertEmail: "",
  });

  const [editingId, setEditingId] = useState(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const fetchMonitors = async () => {
    try {
      setLoading(true);

      const response = await api.get("/monitor");

      setMonitors(
        Array.isArray(response.data)
          ? response.data
          : []
      );

      setError("");
    } catch (err) {
      console.error("Failed to load monitors:", err);

      setError(
        err.response?.data?.message ||
          "Failed to load monitors"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMonitors();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setSaving(true);
      setError("");

      if (editingId) {
        await api.put(
          `/monitor/${editingId}`,
          form
        );
      } else {
        await api.post("/monitor", form);
      }

      resetForm();
      await fetchMonitors();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to save monitor"
      );
    } finally {
      setSaving(false);
    }
  };

  const resetForm = () => {
    setEditingId(null);

    setForm({
      name: "",
      url: "",
      isPublic: true,
      webHookUrl: "",
      alertEmail: "",
    });
  };

  const handleEdit = (monitor) => {
    setEditingId(monitor.id);

    setForm({
      name: monitor.name || "",
      url: monitor.url || "",
      isPublic: monitor.isPublic ?? true,
      webHookUrl: monitor.webHookUrl || "",
      alertEmail: monitor.alertEmail || "",
    });

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this monitor?")) {
      return;
    }

    try {
      await api.delete(`/monitor/${id}`);
      await fetchMonitors();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to delete monitor"
      );
    }
  };

  const handleTogglePublic = async (monitor) => {
    try {
      await api.patch(
        `/monitor/${monitor.id}`,
        {
          isPublic: !monitor.isPublic,
        }
      );

      await fetchMonitors();
    } catch (err) {
      console.error(err);
      setError("Failed to update monitor");
    }
  };

  const handleToggleActive = async (monitor) => {
    try {
      await api.patch(
        `/monitor/${monitor.id}`,
        {
          isActive: !monitor.isActive,
        }
      );

      await fetchMonitors();
    } catch (err) {
      console.error(err);
      setError("Failed to update monitor");
    }
  };

  const activeCount = monitors.filter(
    (m) => m.isActive
  ).length;

  const publicCount = monitors.filter(
    (m) => m.isPublic
  ).length;

  const downCount = monitors.filter(
    (m) => m.isActive && m.consecutiveFailures >= 3
  ).length;

  return (
    <div className="min-h-screen bg-slate-100">

      <Navbar />

      <main className="max-w-7xl mx-auto px-6 py-8">

        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900">
            Dashboard
          </h1>

          <p className="text-slate-500 mt-1">
            Monitor your websites and APIs in real time.
          </p>
        </div>

        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}

        {/* Statistics */}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">

          <div className="bg-white rounded-xl shadow-sm p-5 border border-slate-200">
            <p className="text-sm text-slate-500">
              Total Monitors
            </p>

            <p className="text-3xl font-bold text-slate-900 mt-2">
              {monitors.length}
            </p>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-5 border border-slate-200">
            <p className="text-sm text-slate-500">
              Active
            </p>

            <p className="text-3xl font-bold text-green-600 mt-2">
              {activeCount}
            </p>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-5 border border-slate-200">
            <p className="text-sm text-slate-500">
              Public
            </p>

            <p className="text-3xl font-bold text-blue-600 mt-2">
              {publicCount}
            </p>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-5 border border-slate-200">
            <p className="text-sm text-slate-500">
              Down
            </p>

            <p className="text-3xl font-bold text-red-600 mt-2">
              {downCount}
            </p>
          </div>

        </div>

        {/* Create/Edit Form */}

        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 mb-8">

          <div className="flex justify-between items-center mb-6">

            <div>
              <h2 className="text-xl font-bold text-slate-900">
                {editingId
                  ? "Edit Monitor"
                  : "Add Monitor"}
              </h2>

              <p className="text-sm text-slate-500 mt-1">
                Configure what you want to monitor.
              </p>
            </div>

            {editingId && (
              <button
                onClick={resetForm}
                className="text-sm text-slate-500 hover:text-slate-900"
              >
                Cancel edit
              </button>
            )}

          </div>

          <form onSubmit={handleSubmit}>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Monitor Name
                </label>

                <input
                  value={form.name}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      name: e.target.value,
                    })
                  }
                  placeholder="My Website"
                  required
                  className="w-full border border-slate-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  URL
                </label>

                <input
                  value={form.url}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      url: e.target.value,
                    })
                  }
                  placeholder="https://example.com"
                  required
                  className="w-full border border-slate-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Alert Email
                </label>

                <input
                  type="email"
                  value={form.alertEmail}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      alertEmail: e.target.value,
                    })
                  }
                  placeholder="alerts@example.com"
                  className="w-full border border-slate-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Webhook URL
                </label>

                <input
                  value={form.webHookUrl}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      webHookUrl: e.target.value,
                    })
                  }
                  placeholder="https://hooks.slack.com/..."
                  className="w-full border border-slate-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>

            </div>

            <div className="flex items-center gap-6 mt-5">

              <label className="flex items-center gap-2 text-sm text-slate-700">
                <input
                  type="checkbox"
                  checked={form.isPublic}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      isPublic: e.target.checked,
                    })
                  }
                  className="w-4 h-4"
                />

                Show on public status page
              </label>

            </div>

            <div className="mt-6 flex gap-3">

              <button
                disabled={saving}
                className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-semibold px-6 py-3 rounded-lg"
              >
                {saving
                  ? "Saving..."
                  : editingId
                  ? "Update Monitor"
                  : "Add Monitor"}
              </button>

              {editingId && (
                <button
                  type="button"
                  onClick={resetForm}
                  className="px-6 py-3 rounded-lg border border-slate-300 hover:bg-slate-50"
                >
                  Cancel
                </button>
              )}

            </div>

          </form>
        </div>

        {/* Monitor List */}

        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">

          <div className="px-6 py-5 border-b border-slate-200">
            <h2 className="text-xl font-bold">
              Your Monitors
            </h2>
          </div>

          {loading ? (
            <div className="p-10 text-center text-slate-500">
              Loading monitors...
            </div>
          ) : monitors.length === 0 ? (
            <div className="p-10 text-center">

              <p className="text-lg font-semibold text-slate-700">
                No monitors yet
              </p>

              <p className="text-slate-500 mt-1">
                Add your first monitor above.
              </p>

            </div>
          ) : (
            <div className="divide-y divide-slate-200">

              {monitors.map((monitor) => {

                const isDown =
                  monitor.isActive &&
                  monitor.consecutiveFailures >= 3;

                return (
                  <div
                    key={monitor.id}
                    className="p-6 hover:bg-slate-50 transition"
                  >

                    <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-5">

                      <div className="min-w-0">

                        <div className="flex items-center gap-3">

                          <h3 className="font-bold text-lg text-slate-900">
                            {monitor.name}
                          </h3>

                          <span
                            className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                              isDown
                                ? "bg-red-100 text-red-700"
                                : monitor.isActive
                                ? "bg-green-100 text-green-700"
                                : "bg-slate-200 text-slate-600"
                            }`}
                          >
                            {isDown
                              ? "DOWN"
                              : monitor.isActive
                              ? "ACTIVE"
                              : "PAUSED"}
                          </span>

                        </div>

                        <p className="text-sm text-slate-500 mt-1 break-all">
                          {monitor.url}
                        </p>

                        <div className="flex flex-wrap gap-2 mt-3">

                          <span className="text-xs bg-slate-100 px-3 py-1 rounded-full">
                            Failures:{" "}
                            {monitor.consecutiveFailures ?? 0}
                          </span>

                          <span
                            className={`text-xs px-3 py-1 rounded-full ${
                              monitor.isPublic
                                ? "bg-blue-100 text-blue-700"
                                : "bg-slate-100 text-slate-600"
                            }`}
                          >
                            {monitor.isPublic
                              ? "Public"
                              : "Private"}
                          </span>

                        </div>

                      </div>

                      <div className="flex flex-wrap gap-2">

                        <Link
                          to={`/dashboard/monitor/${monitor.id}`}
                          className="px-4 py-2 rounded-lg bg-blue-50 text-blue-700 hover:bg-blue-100 text-sm font-medium"
                        >
                          Details
                        </Link>

                        <button
                          onClick={() =>
                            handleToggleActive(monitor)
                          }
                          className="px-4 py-2 rounded-lg bg-slate-100 hover:bg-slate-200 text-sm font-medium"
                        >
                          {monitor.isActive
                            ? "Pause"
                            : "Activate"}
                        </button>

                        <button
                          onClick={() =>
                            handleTogglePublic(monitor)
                          }
                          className="px-4 py-2 rounded-lg bg-indigo-50 text-indigo-700 hover:bg-indigo-100 text-sm font-medium"
                        >
                          {monitor.isPublic
                            ? "Make Private"
                            : "Make Public"}
                        </button>

                        <button
                          onClick={() =>
                            handleEdit(monitor)
                          }
                          className="px-4 py-2 rounded-lg bg-amber-50 text-amber-700 hover:bg-amber-100 text-sm font-medium"
                        >
                          Edit
                        </button>

                        <button
                          onClick={() =>
                            handleDelete(monitor.id)
                          }
                          className="px-4 py-2 rounded-lg bg-red-50 text-red-700 hover:bg-red-100 text-sm font-medium"
                        >
                          Delete
                        </button>

                      </div>

                    </div>

                  </div>
                );
              })}

            </div>
          )}

        </div>

      </main>
    </div>
  );
}