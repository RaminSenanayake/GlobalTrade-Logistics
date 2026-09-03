import React, { useState, useEffect } from 'react';
import { useAuth } from './context/AuthContext';
import { useToast } from './components/common/Toast';
import { api } from './services/api';

import { Navbar } from './components/common/Navbar';
import { Sidebar } from './components/common/Sidebar';

import { LoginView } from './views/LoginView';
import { DashboardView } from './views/DashboardView';
import { ShipmentsView } from './views/ShipmentsView';
import { BookingWizardView } from './views/BookingWizardView';
import { CustomsView } from './views/CustomsView';
import { VendorsView } from './views/VendorsView';
import { RoutesView } from './views/RoutesView';
import { BatchOperationsView } from './views/BatchOperationsView';
import { InventoryView } from './views/InventoryView';
import { UserManagementView } from './views/UserManagementView';

export const App = () => {
  const { user, isAuthenticated, loading: authLoading, login } = useAuth();
  const { success: toastSuccess, error: toastError } = useToast();

  const [currentView, setCurrentView] = useState('dashboard');
  const [systemStatus, setSystemStatus] = useState(null);

  const fetchStatus = async () => {
    try {
      const res = await api.monitoring.getStatus();
      setSystemStatus(res);
    } catch (err) {
      // Background poll failure silent
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      fetchStatus();
      const interval = setInterval(fetchStatus, 15000);
      return () => clearInterval(interval);
    }
  }, [isAuthenticated]);

  const handleQuickLogin = async (username, password) => {
    try {
      await login(username, password);
      toastSuccess(`Switched to user '${username}'`);
    } catch (err) {
      toastError(`Failed switching role: ${err.message}`);
    }
  };

  if (authLoading) {
    return (
      <div className="min-h-screen w-full flex items-center justify-center bg-[#0b0f19]">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 border-3 border-blue-500 border-t-transparent rounded-full animate-spin" />
          <p className="text-xs text-gray-400 font-mono">Initializing Enterprise Client...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <LoginView />;
  }

  const renderCurrentView = () => {
    switch (currentView) {
      case 'dashboard':
        return <DashboardView onNavigate={(view) => setCurrentView(view)} />;
      case 'shipments':
        return <ShipmentsView />;
      case 'booking':
        return <BookingWizardView onShipmentCreated={() => setCurrentView('shipments')} />;
      case 'customs':
        return <CustomsView />;
      case 'vendors':
        return <VendorsView />;
      case 'routes':
        return <RoutesView />;
      case 'batch':
        return <BatchOperationsView />;
      case 'inventory':
        return <InventoryView />;
      case 'users':
        return <UserManagementView />;
      default:
        return <DashboardView onNavigate={(view) => setCurrentView(view)} />;
    }
  };

  return (
    <div className="flex min-h-screen bg-[#0b0f19] text-gray-100 font-sans">
      {/* Sidebar Navigation */}
      <Sidebar
        currentView={currentView}
        onViewChange={(v) => setCurrentView(v)}
        stats={systemStatus}
      />

      {/* Main Workspace Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-x-hidden">
        <Navbar
          currentView={currentView}
          systemStatus={systemStatus}
          onQuickLogin={handleQuickLogin}
        />

        <main className="flex-1 p-6 md:p-8 overflow-y-auto max-w-7xl w-full mx-auto">
          {renderCurrentView()}
        </main>
      </div>
    </div>
  );
};
