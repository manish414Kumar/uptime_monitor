import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

// Add JWT automatically
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Your backend wraps responses inside { data, error, timestamp }
// This unwraps them automatically.
api.interceptors.response.use(
  (response) => {
    const body = response.data;

    if (
      body &&
      typeof body === "object" &&
      Object.prototype.hasOwnProperty.call(body, "data") &&
      Object.prototype.hasOwnProperty.call(body, "error")
    ) {
      return {
        ...response,
        data: body.data,
      };
    }

    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("email");
      localStorage.removeItem("role");
    }

    return Promise.reject(error);
  }
);

export default api;