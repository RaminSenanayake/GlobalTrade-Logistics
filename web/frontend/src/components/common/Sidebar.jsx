import React from 'react';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Package,
  Sparkles,
  FileCheck2,
  Building2,
  Compass,
  Layers,
  Boxes,
  Users,
  LogOut,
  Ship,
  Globe2
} from 'lucide-react';

export const Sidebar = ({ currentView, onViewChange, stats }) => {
  const { user, logout, hasRole } = useAuth();

  const navItems = [
    {
      id: 'dashboard',
      label: 'Command Center',
      icon: LayoutDashboard,
      badge: stats?.unacknowledgedAlertsCount > 0 ? stats.unacknowledgedAlertsCount : null,
      badgeColor: 'bg-rose-600',
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'CUSTOM_OFFICIAL', 'VENDOR', 'CUSTOMER']
    },
    {
      id: 'shipments',
      label: 'Shipments & Tracking',
      icon: Package,
      badge: stats?.delayedShipmentsCount > 0 ? `${stats.delayedShipmentsCount} delayed` : null,
      badgeColor: 'bg-amber-600',
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'CUSTOM_OFFICIAL', 'VENDOR', 'CUSTOMER']
    },
    {
      id: 'booking',
      label: 'Stateful Booking (EJB)',
      icon: Sparkles,
      tag: 'Wizard',
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'CUSTOMER']
    },
    {
      id: 'customs',
      label: 'Customs & Compliance',
      icon: FileCheck2,
      badge: stats?.pendingCustomsDeclarationsCount > 0 ? stats.pendingCustomsDeclarationsCount : null,
      badgeColor: 'bg-blue-600',
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'CUSTOM_OFFICIAL']
    },
    {
      id: 'vendors',
      label: 'Vendors & Scorecards',
      icon: Building2,
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'VENDOR']
    },
    {
      id: 'routes',
      label: 'Route Optimization',
      icon: Compass,
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'CUSTOMER']
    },
    {
      id: 'batch',
      label: 'Batch & Manifests',
      icon: Layers,
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL']
    },
    {
      id: 'inventory',
      label: 'Warehouse & Stock',
      icon: Boxes,
      badge: stats?.lowStockInventoryCount > 0 ? `${stats.lowStockInventoryCount} low` : null,
      badgeColor: 'bg-rose-500',
      allowedRoles: ['ADMIN', 'LOGISTIC_PERSONNEL', 'VENDOR']
    },
    {
      id: 'users',
      label: 'User Management',
      icon: Users,
      allowedRoles: ['ADMIN']
    }
  ];

  const visibleNavItems = navItems.filter((item) =>
    item.allowedRoles.includes(user?.role || 'CUSTOMER')
  );

  return (
    <aside className="w-64 bg-[#0d1424] border-r border-gray-800 flex flex-col h-screen sticky top-0 flex-shrink-0 select-none z-40">
      {/* Brand Header */}
      <div className="p-5 border-b border-gray-800 flex items-center gap-3">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-400 flex items-center justify-center text-white shadow-lg shadow-blue-500/20">
          <Ship className="w-5 h-5" />
        </div>
        <div>
          <h1 className="text-sm font-extrabold text-white tracking-tight leading-none bg-gradient-to-r from-white via-gray-200 to-blue-200 bg-clip-text text-transparent">
            GLOBALTRADE
          </h1>
          <span className="text-[10px] font-bold tracking-widest text-cyan-400 uppercase mt-0.5 block">
            Logistics Platform
          </span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1 custom-scrollbar">
        <div className="px-3 pb-2 text-[10px] font-bold uppercase tracking-wider text-gray-400">
          Modules & Workflows
        </div>

        {visibleNavItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentView === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onViewChange(item.id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-semibold transition-all group ${
                isActive
                  ? 'bg-gradient-to-r from-blue-600/20 to-blue-600/5 text-blue-300 border border-blue-500/30 shadow-sm shadow-blue-500/10'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/60'
              }`}
            >
              <Icon className={`w-4 h-4 transition-colors ${isActive ? 'text-blue-400' : 'text-gray-500 group-hover:text-gray-300'}`} />
              <span className="flex-1 text-left truncate">{item.label}</span>
              
              {item.tag && (
                <span className="text-[9px] px-1.5 py-0.5 rounded font-bold uppercase tracking-wide bg-blue-500/20 text-blue-300 border border-blue-500/30">
                  {item.tag}
                </span>
              )}

              {item.badge && (
                <span className={`text-[10px] px-1.5 py-0.5 rounded-full font-bold text-white ${item.badgeColor} shadow-sm animate-pulse`}>
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* Session Footer */}
      <div className="p-3 border-t border-gray-800 bg-[#090d18]">
        <div className="flex items-center justify-between p-2 rounded-xl bg-gray-900/80 border border-gray-800/80">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="w-8 h-8 rounded-lg bg-blue-900/60 border border-blue-500/40 text-blue-300 flex items-center justify-center font-bold text-xs">
              {user?.username?.substring(0, 2).toUpperCase() || 'GT'}
            </div>
            <div className="truncate">
              <div className="text-xs font-bold text-gray-200 truncate">{user?.username}</div>
              <div className="text-[10px] text-gray-400 font-mono capitalize">{user?.role?.toLowerCase()}</div>
            </div>
          </div>
          <button
            onClick={logout}
            className="p-1.5 text-gray-400 hover:text-rose-400 hover:bg-rose-950/40 rounded-lg transition-colors"
            title="Sign Out"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};
