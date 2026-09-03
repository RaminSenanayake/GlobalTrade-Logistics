import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useToast } from '../components/common/Toast';
import {
  Activity,
  AlertTriangle,
  Package,
  Clock,
  FileCheck2,
  Boxes,
  CheckCircle2,
  RefreshCw,
  Zap,
  ArrowUpRight,
  ShieldAlert,
  Server
} from 'lucide-react';

export const DashboardView = ({ onNavigate }) => {
  const { error: toastError, success: toastSuccess } = useToast();

  const [status, setStatus] = useState(null);
  const [alerts, setAlerts] = useState([]);
  const [metrics, setMetrics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadDashboardData = async () => {
    try {
      const [statusRes, alertsRes, metricsRes] = await Promise.all([
        api.monitoring.getStatus(),
        api.monitoring.getAlerts(),
        api.monitoring.getMetrics(15)
      ]);
      setStatus(statusRes);
      setAlerts(alertsRes || []);
      setMetrics(metricsRes || []);
    } catch (err) {
      toastError('Failed to load dashboard telemetry: ' + err.message);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
    const interval = setInterval(loadDashboardData, 10000); // 10s live polling
    return () => clearInterval(interval);
  }, []);

  const handleRefresh = () => {
    setRefreshing(true);
    loadDashboardData();
  };

  const handleAcknowledgeAlert = async (id) => {
    try {
      await api.monitoring.acknowledgeAlert(id);
      toastSuccess(`Alert #${id} acknowledged.`);
      setAlerts((prev) => prev.filter((a) => a.id !== id));
      if (status) {
        setStatus({ ...status, unacknowledgedAlertsCount: Math.max(0, status.unacknowledgedAlertsCount - 1) });
      }
    } catch (err) {
      toastError('Failed to acknowledge alert: ' + err.message);
    }
  };

  const getSeverityBadge = (severity) => {
    switch (severity) {
      case 'CRITICAL':
        return 'bg-rose-500/20 text-rose-300 border-rose-500/40 animate-pulse';
      case 'HIGH':
        return 'bg-amber-500/20 text-amber-300 border-amber-500/40';
      case 'MEDIUM':
        return 'bg-blue-500/20 text-blue-300 border-blue-500/40';
      case 'LOW':
      default:
        return 'bg-gray-500/20 text-gray-300 border-gray-500/40';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
          <p className="text-xs text-gray-400 font-mono">Loading telemetry stream...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Top Banner with Quick Actions & Manual Refresh */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-[#141e33] to-[#101726] p-6 rounded-3xl border border-gray-800 shadow-xl">
        <div>
          <div className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 shadow-[0_0_10px_#10b981]" />
            <h1 className="text-xl font-black text-white tracking-tight">Enterprise Supply Chain Command Center</h1>
          </div>
          <p className="text-xs text-gray-400 mt-1">
            Real-time monitoring across EJB micro-services, background schedulers, and customs compliance gateways.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <button
            onClick={handleRefresh}
            disabled={refreshing}
            className="flex items-center gap-2 px-3.5 py-2 rounded-xl bg-gray-800/80 hover:bg-gray-700 border border-gray-700 text-xs font-semibold text-gray-200 transition"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${refreshing ? 'animate-spin text-blue-400' : ''}`} />
            <span>Sync</span>
          </button>
          <button
            onClick={() => onNavigate('booking')}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20 transition"
          >
            <Zap className="w-3.5 h-3.5" />
            <span>New Booking Wizard</span>
          </button>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {/* Active Shipments */}
        <div
          onClick={() => onNavigate('shipments')}
          className="bg-[#121929] border border-gray-800 hover:border-blue-500/50 p-4 rounded-2xl cursor-pointer transition group"
        >
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-[11px] font-bold uppercase tracking-wider">Active</span>
            <Package className="w-4 h-4 text-blue-400 group-hover:scale-110 transition-transform" />
          </div>
          <div className="text-2xl font-black text-white mt-2">
            {status?.activeShipmentsCount ?? 0}
          </div>
          <div className="text-[10px] text-gray-400 mt-1 flex items-center justify-between">
            <span>In transit & hubs</span>
            <ArrowUpRight className="w-3 h-3 text-blue-400" />
          </div>
        </div>

        {/* Delayed Shipments */}
        <div
          onClick={() => onNavigate('shipments')}
          className="bg-[#121929] border border-gray-800 hover:border-amber-500/50 p-4 rounded-2xl cursor-pointer transition group"
        >
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-[11px] font-bold uppercase tracking-wider">Delayed</span>
            <Clock className="w-4 h-4 text-amber-400 group-hover:scale-110 transition-transform" />
          </div>
          <div className="text-2xl font-black text-amber-400 mt-2">
            {status?.delayedShipmentsCount ?? 0}
          </div>
          <div className="text-[10px] text-gray-400 mt-1 flex items-center justify-between">
            <span>Scheduler flagged</span>
            <ArrowUpRight className="w-3 h-3 text-amber-400" />
          </div>
        </div>

        {/* Pending Customs */}
        <div
          onClick={() => onNavigate('customs')}
          className="bg-[#121929] border border-gray-800 hover:border-indigo-500/50 p-4 rounded-2xl cursor-pointer transition group"
        >
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-[11px] font-bold uppercase tracking-wider">Customs</span>
            <FileCheck2 className="w-4 h-4 text-indigo-400 group-hover:scale-110 transition-transform" />
          </div>
          <div className="text-2xl font-black text-white mt-2">
            {status?.pendingCustomsDeclarationsCount ?? 0}
          </div>
          <div className="text-[10px] text-gray-400 mt-1 flex items-center justify-between">
            <span>Awaiting review</span>
            <ArrowUpRight className="w-3 h-3 text-indigo-400" />
          </div>
        </div>

        {/* Low Stock Alerts */}
        <div
          onClick={() => onNavigate('inventory')}
          className="bg-[#121929] border border-gray-800 hover:border-rose-500/50 p-4 rounded-2xl cursor-pointer transition group"
        >
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-[11px] font-bold uppercase tracking-wider">Low Stock</span>
            <Boxes className="w-4 h-4 text-rose-400 group-hover:scale-110 transition-transform" />
          </div>
          <div className="text-2xl font-black text-rose-400 mt-2">
            {status?.lowStockInventoryCount ?? 0}
          </div>
          <div className="text-[10px] text-gray-400 mt-1 flex items-center justify-between">
            <span>Under threshold</span>
            <ArrowUpRight className="w-3 h-3 text-rose-400" />
          </div>
        </div>

        {/* Unacknowledged Alerts */}
        <div className="bg-[#121929] border border-gray-800 p-4 rounded-2xl">
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-[11px] font-bold uppercase tracking-wider">Alerts</span>
            <AlertTriangle className="w-4 h-4 text-rose-400" />
          </div>
          <div className="text-2xl font-black text-white mt-2">
            {status?.unacknowledgedAlertsCount ?? 0}
          </div>
          <div className="text-[10px] text-gray-400 mt-1">Requires action</div>
        </div>

        {/* Interceptor Telemetry Latency */}
        <div className="bg-[#121929] border border-gray-800 p-4 rounded-2xl">
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-[11px] font-bold uppercase tracking-wider">Avg Latency</span>
            <Server className="w-4 h-4 text-cyan-400" />
          </div>
          <div className="text-2xl font-black text-cyan-400 mt-2">
            {status?.averageExecutionTimeMs !== undefined ? `${status.averageExecutionTimeMs.toFixed(1)} ms` : '—'}
          </div>
          <div className="text-[10px] text-gray-400 mt-1">Interceptor metrics</div>
        </div>
      </div>

      {/* Main Grid: Live Alerts & Interceptor Telemetry */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Live Alerts Panel */}
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl flex flex-col">
          <div className="flex items-center justify-between pb-4 border-b border-gray-800">
            <div className="flex items-center gap-2.5">
              <ShieldAlert className="w-5 h-5 text-rose-400" />
              <h2 className="text-sm font-bold text-white tracking-tight">Active Supply Chain Alerts</h2>
            </div>
            <span className="text-xs font-mono text-gray-400">
              {alerts.length} unacknowledged
            </span>
          </div>

          <div className="flex-1 mt-4 space-y-3 overflow-y-auto max-h-96 pr-1 custom-scrollbar">
            {alerts.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-12 text-center">
                <CheckCircle2 className="w-10 h-10 text-emerald-400 mb-2" />
                <p className="text-sm font-bold text-gray-300">All Systems Nominal</p>
                <p className="text-xs text-gray-400">No unacknowledged supply chain anomalies or delays detected.</p>
              </div>
            ) : (
              alerts.map((alert) => (
                <div
                  key={alert.id}
                  className="p-4 rounded-2xl bg-gray-900/90 border border-gray-800 hover:border-gray-700 transition flex items-start justify-between gap-3"
                >
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getSeverityBadge(alert.severity)}`}>
                        {alert.severity}
                      </span>
                      <span className="text-[10px] font-bold text-cyan-400 uppercase tracking-wider font-mono">
                        {alert.type}
                      </span>
                    </div>
                    <p className="text-xs text-gray-200 font-medium leading-relaxed mt-1">
                      {alert.message}
                    </p>
                    <div className="text-[10px] text-gray-400 font-mono">
                      Tracking: <span className="text-gray-300 font-semibold">{alert.trackingNumber || 'N/A'}</span> &bull; {new Date(alert.createdAt).toLocaleString()}
                    </div>
                  </div>

                  <button
                    onClick={() => handleAcknowledgeAlert(alert.id)}
                    className="flex-shrink-0 text-[11px] font-semibold px-2.5 py-1 rounded-lg bg-gray-800 hover:bg-emerald-600/30 text-gray-300 hover:text-emerald-300 border border-gray-700 hover:border-emerald-500/40 transition"
                  >
                    Acknowledge
                  </button>
                </div>
              ))
            )}
          </div>
        </div>

        {/* EJB Performance Telemetry Metrics */}
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl flex flex-col">
          <div className="flex items-center justify-between pb-4 border-b border-gray-800">
            <div className="flex items-center gap-2.5">
              <Activity className="w-5 h-5 text-cyan-400" />
              <h2 className="text-sm font-bold text-white tracking-tight">EJB Performance Telemetry</h2>
            </div>
            <span className="text-[11px] text-gray-400 font-mono">Audited by Interceptors</span>
          </div>

          <div className="flex-1 mt-4 overflow-y-auto max-h-96 custom-scrollbar">
            {metrics.length === 0 ? (
              <div className="py-12 text-center text-xs text-gray-400">
                No telemetry recorded yet. Perform bean operations to populate.
              </div>
            ) : (
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-gray-800 text-[10px] uppercase font-bold text-gray-400">
                    <th className="pb-2">Target Component</th>
                    <th className="pb-2">Method</th>
                    <th className="pb-2 text-right">Duration</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800/60 font-mono">
                  {metrics.map((m) => {
                    const shortClass = m.className ? m.className.split('.').pop() : 'Bean';
                    const isFast = m.executionTimeMs < 50;
                    return (
                      <tr key={m.id} className="hover:bg-gray-800/30 transition-colors">
                        <td className="py-2.5 text-gray-300 font-semibold truncate max-w-[160px]">
                          {shortClass}
                        </td>
                        <td className="py-2.5 text-blue-400 truncate max-w-[140px]">
                          {m.methodName}()
                        </td>
                        <td className="py-2.5 text-right font-bold">
                          <span className={isFast ? 'text-emerald-400' : 'text-amber-400'}>
                            {m.executionTimeMs} ms
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
