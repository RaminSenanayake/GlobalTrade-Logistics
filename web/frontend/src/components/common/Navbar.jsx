import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Activity, Shield, User, LogOut, ChevronRight } from 'lucide-react';

export const Navbar = ({ currentView, systemStatus, onQuickLogin }) => {
  const { user, logout } = useAuth();

  const getRoleBadgeColor = (role) => {
    switch (role) {
      case 'ADMIN': return 'bg-purple-500/20 text-purple-300 border-purple-500/30';
      case 'LOGISTIC_PERSONNEL': return 'bg-blue-500/20 text-blue-300 border-blue-500/30';
      case 'CUSTOM_OFFICIAL': return 'bg-amber-500/20 text-amber-300 border-amber-500/30';
      case 'VENDOR': return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30';
      case 'CUSTOMER': return 'bg-cyan-500/20 text-cyan-300 border-cyan-500/30';
      default: return 'bg-gray-500/20 text-gray-300 border-gray-500/30';
    }
  };

  const getViewTitle = () => {
    switch (currentView) {
      case 'dashboard': return 'Operations Command Center';
      case 'shipments': return 'Shipment Lifecycle & Tracking';
      case 'booking': return 'Conversational Booking Session';
      case 'customs': return 'Customs Compliance & Declarations';
      case 'vendors': return 'Vendor Performance & Scorecards';
      case 'routes': return 'Multimodal Route Optimization';
      case 'batch': return 'Batch Logistics & Cargo Manifest';
      case 'inventory': return 'Warehouse & Stock Inventory';
      case 'users': return 'Enterprise User Access Control';
      default: return 'Dashboard';
    }
  };

  const isHealthy = !systemStatus || systemStatus.systemHealthStatus === 'HEALTHY' || systemStatus.systemHealthStatus === 'OPTIMAL';

  return (
    <header className="h-16 bg-[#111827]/90 backdrop-blur-md border-b border-gray-800 px-6 flex items-center justify-between sticky top-0 z-30">
      {/* Left: View Breadcrumb */}
      <div className="flex items-center gap-3">
        <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">Platform</span>
        <ChevronRight className="w-3.5 h-3.5 text-gray-600" />
        <h2 className="text-base font-bold text-white tracking-tight">{getViewTitle()}</h2>
      </div>

      {/* Right: Quick Role Switcher + System Health + User Profile */}
      <div className="flex items-center gap-4">
        {/* System Health Badge */}
        <div className="hidden md:flex items-center gap-2 px-3 py-1 rounded-full bg-gray-800/80 border border-gray-700/60 text-xs font-medium">
          <span className={`w-2 h-2 rounded-full ${isHealthy ? 'bg-emerald-500 shadow-[0_0_8px_#10b981]' : 'bg-amber-500 shadow-[0_0_8px_#f59e0b]'} animate-pulse`} />
          <span className="text-gray-300 font-mono">
            {systemStatus?.systemHealthStatus || 'ONLINE'}
          </span>
          {systemStatus?.averageExecutionTimeMs !== undefined && (
            <span className="text-gray-400 pl-1 border-l border-gray-700">
              {systemStatus.averageExecutionTimeMs.toFixed(1)} ms avg
            </span>
          )}
        </div>

        {/* Quick Demo Role Switcher Dropdown/Pills */}
        <div className="hidden lg:flex items-center gap-1 bg-gray-900/90 p-1 rounded-lg border border-gray-800 text-[11px]">
          <span className="text-gray-400 px-1.5 font-semibold uppercase tracking-wider text-[10px]">Switch Role:</span>
          <button
            onClick={() => onQuickLogin('admin', 'Admin@123')}
            className={`px-2 py-0.5 rounded transition ${user?.username === 'admin' ? 'bg-purple-600 text-white font-bold' : 'text-gray-400 hover:text-white hover:bg-gray-800'}`}
            title="Log in as Admin"
          >
            Admin
          </button>
          <button
            onClick={() => onQuickLogin('logistics_mgr', 'Logistics@123')}
            className={`px-2 py-0.5 rounded transition ${user?.username === 'logistics_mgr' ? 'bg-blue-600 text-white font-bold' : 'text-gray-400 hover:text-white hover:bg-gray-800'}`}
            title="Log in as Logistics Manager"
          >
            Logistics
          </button>
          <button
            onClick={() => onQuickLogin('customs_agent', 'Customs@123')}
            className={`px-2 py-0.5 rounded transition ${user?.username === 'customs_agent' ? 'bg-amber-600 text-white font-bold' : 'text-gray-400 hover:text-white hover:bg-gray-800'}`}
            title="Log in as Customs Official"
          >
            Customs
          </button>
          <button
            onClick={() => onQuickLogin('vendor_rep', 'Vendor@123')}
            className={`px-2 py-0.5 rounded transition ${user?.username === 'vendor_rep' ? 'bg-emerald-600 text-white font-bold' : 'text-gray-400 hover:text-white hover:bg-gray-800'}`}
            title="Log in as Vendor"
          >
            Vendor
          </button>
          <button
            onClick={() => onQuickLogin('customer1', 'Customer@123')}
            className={`px-2 py-0.5 rounded transition ${user?.username === 'customer1' ? 'bg-cyan-600 text-white font-bold' : 'text-gray-400 hover:text-white hover:bg-gray-800'}`}
            title="Log in as Customer"
          >
            Customer
          </button>
        </div>

        {/* User Badge */}
        {user && (
          <div className="flex items-center gap-2.5 pl-2 border-l border-gray-800">
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-bold text-xs shadow-md">
              {user.username.charAt(0).toUpperCase()}
            </div>
            <div className="hidden sm:block text-left">
              <div className="text-xs font-bold text-gray-200 leading-tight">{user.username}</div>
              <span className={`inline-block text-[10px] px-1.5 py-0.2 rounded border font-semibold ${getRoleBadgeColor(user.role)}`}>
                {user.role}
              </span>
            </div>
            <button
              onClick={logout}
              className="p-1.5 text-gray-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-lg transition-colors ml-1"
              title="Sign Out"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        )}
      </div>
    </header>
  );
};
