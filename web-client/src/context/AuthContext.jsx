import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user_data');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('auth_token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // If token exists, optionally verify/fetch latest profile
    if (token) {
      authAPI.getProfile()
        .then((res) => {
          if (res.user) {
            setUser(res.user);
            localStorage.setItem('user_data', JSON.stringify(res.user));
          }
        })
        .catch(() => {
          // If token expired or invalid, keep cached user or allow logout
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const data = await authAPI.login(email, password);
    const authToken = data.token || data.accessToken || data.jwt;
    const userData = data.user || {
      name: data.name || (email.split('@')[0]),
      email: email,
      role: data.role || 'customer',
    };

    if (authToken) {
      localStorage.setItem('auth_token', authToken);
      setToken(authToken);
    }
    localStorage.setItem('user_data', JSON.stringify(userData));
    localStorage.setItem('user_role', userData.role);
    localStorage.setItem('customer_name', userData.name);
    setUser(userData);
    return userData;
  };

  const register = async (userData) => {
    const data = await authAPI.register(userData);
    const authToken = data.token || data.accessToken;
    const resolvedUser = data.user || userData;

    if (authToken) {
      localStorage.setItem('auth_token', authToken);
      setToken(authToken);
    }
    localStorage.setItem('user_data', JSON.stringify(resolvedUser));
    localStorage.setItem('user_role', resolvedUser.role);
    localStorage.setItem('customer_name', resolvedUser.name);
    setUser(resolvedUser);
    return resolvedUser;
  };

  const logout = () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_data');
    localStorage.removeItem('user_role');
    localStorage.removeItem('customer_name');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
