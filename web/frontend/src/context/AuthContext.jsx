import React, { createContext, useContext, useState, useEffect } from 'react';
import { api, getUser, setAuthData, clearAuthData, getAccessToken } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(getUser());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = getUser();
    const token = getAccessToken();
    if (savedUser && token) {
      setUser(savedUser);
    } else {
      setUser(null);
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    const response = await api.auth.login(username, password);
    setAuthData(response);
    const userInfo = {
      username: response.username,
      role: response.role
    };
    setUser(userInfo);
    return userInfo;
  };

  const logout = () => {
    clearAuthData();
    setUser(null);
  };

  const hasRole = (...roles) => {
    if (!user || !user.role) return false;
    return roles.includes(user.role);
  };

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      logout,
      hasRole,
      isAuthenticated: !!user
    }}>
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
