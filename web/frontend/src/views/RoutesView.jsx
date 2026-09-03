import React, { useState } from 'react';
import { api } from '../services/api';
import { useToast } from '../components/common/Toast';
import {
  Compass,
  Plane,
  Ship,
  Train,
  Truck,
  Leaf,
  DollarSign,
  Clock,
  ShieldAlert,
  ArrowRight,
  Zap,
  CheckCircle2,
  Layers
} from 'lucide-react';

export const RoutesView = () => {
  const { error: toastError, success: toastSuccess } = useToast();

  const [form, setForm] = useState({
    origin: 'Port of Los Angeles (USLAX)',
    destination: 'Port of Rotterdam (NLRTM)',
    weight: 2500.0,
    priority: 'COST'
  });

  const [loading, setLoading] = useState(false);
  const [optimalResult, setOptimalResult] = useState(null);
  const [comparisonList, setComparisonList] = useState(null);

  const handleOptimize = async (e) => {
    e?.preventDefault();
    setLoading(true);
    setComparisonList(null);
    try {
      const result = await api.routes.optimize(form.origin, form.destination, form.weight, form.priority);
      setOptimalResult(result);
      toastSuccess('Multimodal route optimization completed.');
    } catch (err) {
      toastError('Route optimization failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCompareAll = async () => {
    setLoading(true);
    try {
      const list = await api.routes.compare(form.origin, form.destination, form.weight);
      setComparisonList(list);
      toastSuccess('Multimodal route comparison matrix loaded.');
    } catch (err) {
      toastError('Comparison failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const getTransportIcon = (mode) => {
    const m = (mode || '').toUpperCase();
    if (m.includes('AIR')) return <Plane className="w-5 h-5 text-cyan-400" />;
    if (m.includes('SEA') || m.includes('OCEAN')) return <Ship className="w-5 h-5 text-blue-400" />;
    if (m.includes('RAIL')) return <Train className="w-5 h-5 text-emerald-400" />;
    return <Truck className="w-5 h-5 text-amber-400" />;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-xl font-bold text-white tracking-tight">Multimodal Route Optimization Engine</h1>
        <p className="text-xs text-gray-400 mt-0.5">
          Algorithmic carrier route selection balancing cost, transit duration, carbon emissions, and geopolitical risk.
        </p>
      </div>

      {/* Input Parameters Panel */}
      <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl">
        <form onSubmit={handleOptimize} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Origin Transit Hub</label>
              <input
                type="text"
                value={form.origin}
                onChange={(e) => setForm({ ...form, origin: e.target.value })}
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white focus:outline-none focus:border-blue-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Destination Transit Hub</label>
              <input
                type="text"
                value={form.destination}
                onChange={(e) => setForm({ ...form, destination: e.target.value })}
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white focus:outline-none focus:border-blue-500"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Total Consignment Weight (kg)</label>
              <input
                type="number"
                step="0.1"
                value={form.weight}
                onChange={(e) => setForm({ ...form, weight: parseFloat(e.target.value) || 0 })}
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white focus:outline-none focus:border-blue-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Optimization Priority Vector</label>
              <select
                value={form.priority}
                onChange={(e) => setForm({ ...form, priority: e.target.value })}
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white focus:outline-none focus:border-blue-500"
              >
                <option value="COST">Lowest Cost ($)</option>
                <option value="SPEED">Fastest Speed (Transit Days)</option>
                <option value="ECO">Eco-Friendly (Lowest Carbon Footprint)</option>
                <option value="RELIABILITY">High Reliability & Minimum Risk</option>
              </select>
            </div>
          </div>

          <div className="flex flex-wrap items-center justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={handleCompareAll}
              disabled={loading}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 border border-gray-700 text-xs font-bold text-gray-200 transition"
            >
              <Layers className="w-4 h-4" />
              <span>Compare All Modes</span>
            </button>

            <button
              type="submit"
              disabled={loading}
              className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-xs font-bold text-white shadow-lg shadow-blue-500/25 transition"
            >
              <Zap className="w-4 h-4" />
              <span>{loading ? 'Optimizing...' : 'Calculate Optimal Route'}</span>
            </button>
          </div>
        </form>
      </div>

      {/* Primary Optimal Route Card */}
      {optimalResult && optimalResult.optimalRoute && (
        <div className="space-y-4 animate-fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 text-emerald-400" />
            <h2 className="text-base font-bold text-white">
              Recommended Optimal Solution (Prioritized: {form.priority})
            </h2>
          </div>

          <div className="bg-gradient-to-r from-emerald-950/40 via-[#132035] to-[#111827] border-2 border-emerald-500/50 rounded-3xl p-6 shadow-2xl relative overflow-hidden">
            <div className="absolute top-4 right-4 bg-emerald-500 text-white text-[10px] font-black uppercase tracking-wider px-3 py-1 rounded-full shadow-lg">
              Optimal Match
            </div>

            <div className="flex items-center gap-3 mb-4">
              <div className="p-3 rounded-2xl bg-emerald-500/20 border border-emerald-500/30">
                {getTransportIcon(optimalResult.optimalRoute.transportMode)}
              </div>
              <div>
                <span className="text-xs font-mono font-bold text-cyan-400 uppercase">
                  {optimalResult.optimalRoute.routeId || 'RT-OPT-01'} &bull; {optimalResult.optimalRoute.carrierCode}
                </span>
                <h3 className="text-lg font-bold text-white">
                  {optimalResult.optimalRoute.transportMode} Multimodal Freight
                </h3>
              </div>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 border-t border-gray-800/80">
              <div className="bg-[#0d1424]/80 p-3 rounded-xl border border-gray-800">
                <span className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1">
                  <DollarSign className="w-3 h-3 text-emerald-400" /> Tariff Cost
                </span>
                <span className="text-lg font-black text-emerald-400 mt-1 block">
                  ${optimalResult.optimalRoute.estimatedCostUSD?.toLocaleString()} USD
                </span>
              </div>

              <div className="bg-[#0d1424]/80 p-3 rounded-xl border border-gray-800">
                <span className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1">
                  <Clock className="w-3 h-3 text-blue-400" /> Est. Transit
                </span>
                <span className="text-lg font-black text-white mt-1 block">
                  {optimalResult.optimalRoute.estimatedDays} Days
                </span>
              </div>

              <div className="bg-[#0d1424]/80 p-3 rounded-xl border border-gray-800">
                <span className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1">
                  <Leaf className="w-3 h-3 text-teal-400" /> Carbon Footprint
                </span>
                <span className="text-lg font-black text-teal-400 mt-1 block">
                  {optimalResult.optimalRoute.carbonEmissionKg} kg CO₂
                </span>
              </div>

              <div className="bg-[#0d1424]/80 p-3 rounded-xl border border-gray-800">
                <span className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1">
                  <ShieldAlert className="w-3 h-3 text-amber-400" /> Risk Index
                </span>
                <span className="text-lg font-black text-amber-400 mt-1 block">
                  {optimalResult.optimalRoute.riskScore?.toFixed(2)} / 1.0
                </span>
              </div>
            </div>
          </div>

          {/* Alternative Routes */}
          {optimalResult.alternativeRoutes && optimalResult.alternativeRoutes.length > 0 && (
            <div className="pt-4">
              <h3 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">
                Alternative Multimodal Options
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {optimalResult.alternativeRoutes.map((alt, idx) => (
                  <div key={idx} className="bg-[#121929] border border-gray-800 p-4 rounded-2xl flex flex-col justify-between">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center gap-2">
                        {getTransportIcon(alt.transportMode)}
                        <span className="text-xs font-bold text-white">{alt.transportMode} ({alt.carrierCode})</span>
                      </div>
                      <span className="text-xs font-mono font-bold text-emerald-400">
                        ${alt.estimatedCostUSD?.toLocaleString()}
                      </span>
                    </div>

                    <div className="grid grid-cols-3 gap-2 text-[11px] font-mono text-gray-300 pt-2 border-t border-gray-800">
                      <div>Days: <strong>{alt.estimatedDays}</strong></div>
                      <div>CO₂: <strong>{alt.carbonEmissionKg} kg</strong></div>
                      <div>Risk: <strong>{alt.riskScore}</strong></div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Side-by-Side Comparison Matrix */}
      {comparisonList && (
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4 animate-fade-in">
          <h2 className="text-base font-bold text-white">Full Multimodal Comparison Matrix</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#0d1424] text-[10px] font-bold text-gray-400 uppercase tracking-wider border-b border-gray-800">
                <tr>
                  <th className="p-3">Mode</th>
                  <th className="p-3">Carrier Code</th>
                  <th className="p-3">Est. Cost</th>
                  <th className="p-3">Transit Time</th>
                  <th className="p-3">CO₂ Emission</th>
                  <th className="p-3">Risk Factor</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800 font-mono text-xs">
                {comparisonList.map((c, i) => (
                  <tr key={i} className="hover:bg-gray-800/40">
                    <td className="p-3 font-sans font-bold text-white flex items-center gap-2">
                      {getTransportIcon(c.transportMode)}
                      <span>{c.transportMode}</span>
                    </td>
                    <td className="p-3 text-cyan-400">{c.carrierCode}</td>
                    <td className="p-3 text-emerald-400 font-bold">${c.estimatedCostUSD?.toLocaleString()} USD</td>
                    <td className="p-3 text-gray-300">{c.estimatedDays} Days</td>
                    <td className="p-3 text-teal-400">{c.carbonEmissionKg} kg</td>
                    <td className="p-3 text-amber-400">{c.riskScore}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
