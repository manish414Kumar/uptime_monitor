import { createContext, useContext, useState } from "react";
import api from "../api/axios";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(
    localStorage.getItem("token")
  );

  const [user, setUser] = useState(() => {
    const email = localStorage.getItem("email");
    const role = localStorage.getItem("role");

    return email
      ? {
          email,
          role,
        }
      : null;
  });

  const login = async (email, password) => {
    const response = await api.post("/login/user", {
      email,
      password,
    });

    // axios.js already unwraps backend {data: ...}
    const { token, email: userEmail, role } = response.data;

    if (!token) {
      throw new Error("Token was not returned by server");
    }

    localStorage.setItem("token", token);
    localStorage.setItem("email", userEmail);
    localStorage.setItem("role", role);

    setToken(token);

    setUser({
      email: userEmail,
      role,
    });

    return response.data;
  };

  const register = async (name, email, password) => {
    return api.post("/login/register", {
      name,
      email,
      password,
    });
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    localStorage.removeItem("role");

    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);