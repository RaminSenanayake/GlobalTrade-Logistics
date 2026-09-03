import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import { Modal } from '../components/common/Modal';
import {
  Building2,
  Plus,
  Star,
  Award,
  ShieldCheck,
  AlertTriangle,
  Clock,
  CheckCircle2,
  UserCheck,
  Mail,
  Globe2,
  BarChart3
} from 'lucide-react';

export const VendorsView = () => {
  const { user, hasRole } = useAuth();
  const { error: toastError, success: toastSuccess } = useToast();

  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');

  // Register Modal
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);
  const [registerForm, setRegisterForm] = useState({
    name: '',
    country: 'USA',
    contactEmail: ''
  });

  // Assign Modal
  const [isAssignOpen, setIsAssignOpen] = useState(false);
  const [assignForm, setAssignForm] = useState({
    trackingNumber: '',
    vendorCode: ''
  });

  // Scorecard Modal
  const [isScorecardOpen, setIsScorecardOpen] = useState(false);
  const [selectedScorecard, setSelectedScorecard] = useState(null);
  const [evaluating, setEvaluating] = useState(false);

  const loadVendors = async () => {
    setLoading(true);
    try {
      const data = await api.vendors.getAll(statusFilter);
      setVendors(data || []);
    } catch (err) {
      toastError('Failed to load vendors: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadVendors();
  }, [statusFilter]);

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const res = await api.vendors.register(
        registerForm.name,
        registerForm.country,
        registerForm.contactEmail
      );
      toastSuccess(`Vendor registered with code ${res.vendorCode}`);
      setIsRegisterOpen(false);
      setRegisterForm({ name: '', country: 'USA', contactEmail: '' });
      loadVendors();
    } catch (err) {
      toastError('Vendor registration failed: ' + err.message);
    }
  };

  const handleAssign = async (e) => {
    e.preventDefault();
    try {
      await api.vendors.assign(assignForm.trackingNumber, assignForm.vendorCode);
      toastSuccess(`Vendor ${assignForm.vendorCode} assigned to shipment ${assignForm.trackingNumber}`);
      setIsAssignOpen(false);
    } catch (err) {
      toastError('Assignment failed: ' + err.message);
    }
  };

  const handleViewScorecard = async (vendorCode) => {
    setEvaluating(true);
    try {
      // Evaluate vendor via EJB scorecard bean
      const scorecard = await api.vendors.evaluate(vendorCode);
      setSelectedScorecard(scorecard);
      setIsScorecardOpen(true);
    } catch (err) {
      toastError('Scorecard evaluation failed: ' + err.message);
    } finally {
      setEvaluating(false);
    }
  };

  const getComplianceBadge = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40';
      case 'PROBATION':
        return 'bg-amber-500/20 text-amber-300 border-amber-500/40';
      case 'SUSPENDED':
        return 'bg-rose-500/20 text-rose-300 border-rose-500/40 animate-pulse';
      case 'UNDER_REVIEW':
      default:
        return 'bg-blue-500/20 text-blue-300 border-blue-500/40';
    }
  };

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-white tracking-tight">Vendor Directory & SLA Performance</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            Evaluate logistics carriers, on-time delivery metrics, and SLA compliance scorecards.
          </p>
        </div>

        {hasRole('ADMIN', 'LOGISTIC_PERSONNEL') && (
          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsAssignOpen(true)}
              className="px-3.5 py-2 rounded-xl bg-gray-800 hover:bg-gray-700 border border-gray-700 text-xs font-semibold text-gray-200 transition"
            >
              Assign to Shipment
            </button>
            <button
              onClick={() => setIsRegisterOpen(true)}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20 transition"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Register Vendor</span>
            </button>
          </div>
        )}
      </div>

      {/* Filter Bar */}
      <div className="flex items-center justify-between bg-[#121929] p-3 rounded-2xl border border-gray-800">
        <span className="text-xs font-bold text-gray-400 uppercase tracking-wider pl-2">Filter Compliance Status:</span>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="bg-[#0d1424] border border-gray-700 rounded-xl px-3 py-1.5 text-xs text-gray-300"
        >
          <option value="">All Statuses</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="PROBATION">PROBATION</option>
          <option value="SUSPENDED">SUSPENDED</option>
          <option value="UNDER_REVIEW">UNDER_REVIEW</option>
        </select>
      </div>

      {/* Vendor Grid */}
      {loading ? (
        <div className="py-16 text-center text-xs text-gray-400">Loading vendors...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {vendors.map((v) => (
            <div
              key={v.id || v.vendorCode}
              className="bg-[#121929] border border-gray-800 hover:border-gray-700 rounded-3xl p-5 shadow-xl flex flex-col justify-between transition group"
            >
              <div>
                <div className="flex items-start justify-between gap-2 mb-3">
                  <div>
                    <span className="text-[10px] font-mono font-bold text-cyan-400 bg-cyan-950/50 border border-cyan-500/30 px-2 py-0.5 rounded-md">
                      {v.vendorCode}
                    </span>
                    <h3 className="text-sm font-bold text-white mt-1 group-hover:text-blue-300 transition">
                      {v.name}
                    </h3>
                  </div>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getComplianceBadge(v.complianceStatus)}`}>
                    {v.complianceStatus}
                  </span>
                </div>

                <div className="space-y-1 text-xs text-gray-400 mt-3 pt-3 border-t border-gray-800/80">
                  <div className="flex items-center gap-2">
                    <Globe2 className="w-3.5 h-3.5 text-gray-500" />
                    <span>Jurisdiction: <strong className="text-gray-200">{v.country}</strong></span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Mail className="w-3.5 h-3.5 text-gray-500" />
                    <span className="truncate">{v.contactEmail}</span>
                  </div>
                </div>
              </div>

              <div className="mt-5 pt-3 border-t border-gray-800 flex items-center justify-between">
                <button
                  onClick={() => handleViewScorecard(v.vendorCode)}
                  disabled={evaluating}
                  className="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-xl bg-blue-600/10 hover:bg-blue-600/20 text-blue-300 border border-blue-500/30 text-xs font-bold transition"
                >
                  <BarChart3 className="w-3.5 h-3.5" />
                  <span>Evaluate Performance Scorecard</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Scorecard Modal */}
      <Modal
        isOpen={isScorecardOpen}
        onClose={() => setIsScorecardOpen(false)}
        title={`Vendor Scorecard: ${selectedScorecard?.name}`}
        subtitle={`Audit telemetry for ${selectedScorecard?.vendorCode}`}
      >
        {selectedScorecard && (
          <div className="space-y-6">
            {/* Scorecard Hero Banner */}
            <div className="flex items-center gap-6 p-6 rounded-2xl bg-gradient-to-r from-blue-950/40 via-indigo-950/40 to-gray-900 border border-blue-500/30">
              <div className="w-20 h-20 rounded-full border-4 border-emerald-400 flex flex-col items-center justify-center text-emerald-400 shadow-xl shadow-emerald-500/20 flex-shrink-0">
                <span className="text-xl font-black">{selectedScorecard.performanceRating?.toFixed(0)}</span>
                <span className="text-[9px] font-bold uppercase tracking-wider text-gray-300">Rating</span>
              </div>

              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <h3 className="text-base font-bold text-white">{selectedScorecard.name}</h3>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getComplianceBadge(selectedScorecard.complianceStatus)}`}>
                    {selectedScorecard.complianceStatus}
                  </span>
                </div>
                <p className="text-xs text-gray-300 italic">
                  "{selectedScorecard.recommendation || 'Vendor operates within expected carrier delivery parameters.'}"
                </p>
              </div>
            </div>

            {/* Metrics Breakdown */}
            <div className="grid grid-cols-3 gap-3 text-center">
              <div className="bg-[#111827] p-4 rounded-2xl border border-gray-800">
                <span className="text-[10px] uppercase font-bold text-gray-400">On-Time Delivery</span>
                <div className="text-xl font-black text-emerald-400 mt-1">
                  {selectedScorecard.onTimeDeliveryRate?.toFixed(1)}%
                </div>
              </div>
              <div className="bg-[#111827] p-4 rounded-2xl border border-gray-800">
                <span className="text-[10px] uppercase font-bold text-gray-400">Total Handled</span>
                <div className="text-xl font-black text-white mt-1">
                  {selectedScorecard.totalShipments}
                </div>
              </div>
              <div className="bg-[#111827] p-4 rounded-2xl border border-gray-800">
                <span className="text-[10px] uppercase font-bold text-gray-400">Delayed Shipments</span>
                <div className="text-xl font-black text-amber-400 mt-1">
                  {selectedScorecard.delayedShipments}
                </div>
              </div>
            </div>

            <div className="flex justify-end pt-4 border-t border-gray-800">
              <button
                onClick={() => setIsScorecardOpen(false)}
                className="px-5 py-2 rounded-xl bg-gray-800 text-xs font-semibold text-gray-300"
              >
                Close Scorecard
              </button>
            </div>
          </div>
        )}
      </Modal>

      {/* Register Vendor Modal */}
      <Modal
        isOpen={isRegisterOpen}
        onClose={() => setIsRegisterOpen(false)}
        title="Register Logistics Vendor Partner"
        subtitle="Onboard a freight forwarding carrier to the enterprise registry"
      >
        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Company / Carrier Name</label>
            <input
              type="text"
              placeholder="e.g. Nordic Freight Alliance"
              value={registerForm.name}
              onChange={(e) => setRegisterForm({ ...registerForm, name: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Country ISO Code</label>
              <input
                type="text"
                placeholder="e.g. DEU, SGP, USA"
                value={registerForm.country}
                onChange={(e) => setRegisterForm({ ...registerForm, country: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Contact Email</label>
              <input
                type="email"
                placeholder="operations@carrier.com"
                value={registerForm.contactEmail}
                onChange={(e) => setRegisterForm({ ...registerForm, contactEmail: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsRegisterOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs text-gray-300 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg"
            >
              Register Vendor
            </button>
          </div>
        </form>
      </Modal>

      {/* Assign Vendor Modal */}
      <Modal
        isOpen={isAssignOpen}
        onClose={() => setIsAssignOpen(false)}
        title="Assign Vendor Carrier to Consignment"
        subtitle="Route assignment validated by VendorValidationInterceptor"
      >
        <form onSubmit={handleAssign} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Tracking Number</label>
            <input
              type="text"
              placeholder="e.g. TRK-US-2026-0001"
              value={assignForm.trackingNumber}
              onChange={(e) => setAssignForm({ ...assignForm, trackingNumber: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Vendor Code</label>
            <input
              type="text"
              placeholder="e.g. VND-001"
              value={assignForm.vendorCode}
              onChange={(e) => setAssignForm({ ...assignForm, vendorCode: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
              required
            />
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsAssignOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs text-gray-300 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg"
            >
              Assign Carrier
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
