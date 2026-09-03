import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import { Modal } from '../components/common/Modal';
import {
  FileCheck2,
  Clock,
  CheckCircle,
  AlertCircle,
  Plus,
  Search,
  ShieldAlert,
  ArrowRight,
  Stamp,
  Calendar
} from 'lucide-react';

export const CustomsView = () => {
  const { user, hasRole } = useAuth();
  const { error: toastError, success: toastSuccess } = useToast();

  const [activeTab, setActiveTab] = useState('pending'); // 'pending' or 'deadlines'
  const [declarations, setDeclarations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deadlinesHours, setDeadlinesHours] = useState(24);

  // Compliance Check Tool State
  const [checkTracking, setCheckTracking] = useState('');
  const [complianceResult, setComplianceResult] = useState(null);
  const [checkingCompliance, setCheckingCompliance] = useState(false);

  // File Declaration Modal
  const [isFileOpen, setIsFileOpen] = useState(false);
  const [fileForm, setFileForm] = useState({
    trackingNumber: '',
    originCountry: 'USA',
    destinationCountry: 'DEU',
    cargoDescription: 'Industrial Automation Sensors & Electronics',
    declaredValueUSD: 8500.0,
    tariffCode: 'HS-8542.31',
    dutyFeeUSD: 425.0
  });

  // Review Modal
  const [isReviewOpen, setIsReviewOpen] = useState(false);
  const [selectedDec, setSelectedDec] = useState(null);
  const [reviewForm, setReviewForm] = useState({
    status: 'APPROVED',
    reviewedBy: user?.username || 'customs_agent',
    notes: 'Documentation verified against trade sanctions registry. Passed automated tariff inspection.'
  });

  const loadDeclarations = async () => {
    setLoading(true);
    try {
      if (activeTab === 'deadlines') {
        const data = await api.customs.getApproachingDeadlines(deadlinesHours);
        setDeclarations(data || []);
      } else {
        const data = await api.customs.getPending();
        setDeclarations(data || []);
      }
    } catch (err) {
      toastError('Failed to load customs declarations: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDeclarations();
  }, [activeTab, deadlinesHours]);

  const handleCheckCompliance = async (e) => {
    e.preventDefault();
    if (!checkTracking.trim()) return;
    setCheckingCompliance(true);
    try {
      const res = await api.customs.checkCompliance(checkTracking.trim());
      setComplianceResult(res);
      if (res.compliant) {
        toastSuccess(`Shipment ${checkTracking} is compliant with customs filing.`);
      } else {
        toastError(`Shipment ${checkTracking} is NOT compliant or declaration missing.`);
      }
    } catch (err) {
      toastError('Compliance check error: ' + err.message);
      setComplianceResult(null);
    } finally {
      setCheckingCompliance(false);
    }
  };

  const handleFileDeclaration = async (e) => {
    e.preventDefault();
    try {
      const res = await api.customs.submitDeclaration(fileForm);
      toastSuccess(`Declaration filed successfully: ${res.declarationNumber || 'Recorded'}`);
      setIsFileOpen(false);
      loadDeclarations();
    } catch (err) {
      toastError('Filing failed: ' + err.message);
    }
  };

  const handleReviewDeclaration = async (e) => {
    e.preventDefault();
    try {
      await api.customs.reviewDeclaration(
        selectedDec.declarationNumber,
        reviewForm.status,
        reviewForm.reviewedBy,
        reviewForm.notes
      );
      toastSuccess(`Declaration ${selectedDec.declarationNumber} marked as ${reviewForm.status}.`);
      setIsReviewOpen(false);
      loadDeclarations();
    } catch (err) {
      toastError('Review update failed: ' + err.message);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'APPROVED':
        return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40';
      case 'HOLD':
        return 'bg-rose-500/20 text-rose-300 border-rose-500/40 animate-pulse';
      case 'REJECTED':
        return 'bg-gray-500/20 text-gray-400 border-gray-500/40';
      case 'SUBMITTED':
      case 'PENDING':
      default:
        return 'bg-blue-500/20 text-blue-300 border-blue-500/40';
    }
  };

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-white tracking-tight">Customs Compliance & Declarations</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            Cross-border electronic declarations, automated HS tariff verification, and regulatory enforcement.
          </p>
        </div>

        <button
          onClick={() => setIsFileOpen(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20 transition self-start sm:self-auto"
        >
          <Plus className="w-3.5 h-3.5" />
          <span>File Declaration</span>
        </button>
      </div>

      {/* Compliance Quick Check Tool */}
      <div className="bg-gradient-to-r from-[#141d30] to-[#101726] p-5 rounded-3xl border border-gray-800 shadow-xl">
        <div className="flex items-center gap-2 mb-3">
          <FileCheck2 className="w-4 h-4 text-blue-400" />
          <h2 className="text-xs font-bold text-white uppercase tracking-wider">Fast Compliance Verification Gateway</h2>
        </div>

        <form onSubmit={handleCheckCompliance} className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="w-4 h-4 text-gray-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Enter Tracking Number (e.g. TRK-US-2026-0001)..."
              value={checkTracking}
              onChange={(e) => setCheckTracking(e.target.value)}
              className="w-full bg-[#0d1424] border border-gray-700 rounded-xl py-2.5 pl-10 pr-4 text-xs text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
            />
          </div>
          <button
            type="submit"
            disabled={checkingCompliance || !checkTracking.trim()}
            className="px-5 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-xs font-bold text-gray-200 transition disabled:opacity-40"
          >
            {checkingCompliance ? 'Verifying...' : 'Verify Status'}
          </button>
        </form>

        {complianceResult && (
          <div className={`mt-3 p-3 rounded-xl border flex items-center justify-between text-xs ${
            complianceResult.compliant
              ? 'bg-emerald-950/40 border-emerald-500/40 text-emerald-200'
              : 'bg-rose-950/40 border-rose-500/40 text-rose-200'
          }`}>
            <div className="flex items-center gap-2 font-mono">
              {complianceResult.compliant ? <CheckCircle className="w-4 h-4 text-emerald-400" /> : <AlertCircle className="w-4 h-4 text-rose-400" />}
              <span>Tracking: <strong>{complianceResult.trackingNumber}</strong></span>
            </div>
            <span className="font-bold">
              {complianceResult.compliant ? 'PASSED CUSTOMS AUDIT & DUTY PAID' : 'NON-COMPLIANT / MISSING DECLARATION'}
            </span>
          </div>
        )}
      </div>

      {/* Tabs: Pending Declarations vs Approaching Deadlines */}
      <div className="flex items-center justify-between border-b border-gray-800 pb-2">
        <div className="flex items-center gap-3">
          <button
            onClick={() => setActiveTab('pending')}
            className={`text-xs font-bold pb-2 border-b-2 transition ${
              activeTab === 'pending'
                ? 'border-blue-500 text-blue-400'
                : 'border-transparent text-gray-400 hover:text-gray-200'
            }`}
          >
            Pending Review Declarations
          </button>
          <button
            onClick={() => setActiveTab('deadlines')}
            className={`text-xs font-bold pb-2 border-b-2 transition ${
              activeTab === 'deadlines'
                ? 'border-amber-500 text-amber-400'
                : 'border-transparent text-gray-400 hover:text-gray-200'
            }`}
          >
            Approaching Filing Deadlines
          </button>
        </div>

        {activeTab === 'deadlines' && (
          <div className="flex items-center gap-2 text-xs text-gray-400">
            <span>Window:</span>
            <select
              value={deadlinesHours}
              onChange={(e) => setDeadlinesHours(parseInt(e.target.value))}
              className="bg-[#0d1424] border border-gray-700 rounded-lg px-2 py-1 text-xs text-gray-300"
            >
              <option value="12">12 Hours</option>
              <option value="24">24 Hours</option>
              <option value="48">48 Hours</option>
              <option value="72">72 Hours</option>
            </select>
          </div>
        )}
      </div>

      {/* Declarations Table */}
      <div className="bg-[#121929] border border-gray-800 rounded-3xl overflow-hidden shadow-xl">
        {loading ? (
          <div className="py-16 text-center text-xs text-gray-400">Loading customs registry...</div>
        ) : declarations.length === 0 ? (
          <div className="py-16 text-center text-xs text-gray-400">
            {activeTab === 'pending'
              ? 'No pending declarations awaiting adjudication.'
              : 'No approaching customs filing deadlines in the selected window.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#0d1424] border-b border-gray-800 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <tr>
                  <th className="py-3.5 px-4">Declaration #</th>
                  <th className="py-3.5 px-4">Tracking #</th>
                  <th className="py-3.5 px-4">Cargo & HS Code</th>
                  <th className="py-3.5 px-4">Origin / Dest</th>
                  <th className="py-3.5 px-4">Declared Value</th>
                  <th className="py-3.5 px-4">Duty Fee</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Adjudication</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60 font-mono">
                {declarations.map((d) => (
                  <tr key={d.id || d.declarationNumber} className="hover:bg-gray-800/30 transition">
                    <td className="py-3 px-4 text-blue-400 font-bold">
                      {d.declarationNumber}
                    </td>
                    <td className="py-3 px-4 text-gray-300">
                      {d.trackingNumber}
                    </td>
                    <td className="py-3 px-4 font-sans">
                      <div className="text-gray-200 font-medium truncate max-w-[200px]">
                        {d.cargoDescription}
                      </div>
                      <div className="text-[10px] text-cyan-400 font-mono">
                        HS: {d.tariffCode || 'N/A'}
                      </div>
                    </td>
                    <td className="py-3 px-4 font-sans text-gray-300">
                      {d.originCountry} &rarr; {d.destinationCountry}
                    </td>
                    <td className="py-3 px-4 text-emerald-400 font-semibold">
                      ${d.declaredValueUSD?.toLocaleString()}
                    </td>
                    <td className="py-3 px-4 text-gray-300">
                      ${d.dutyFeeUSD?.toLocaleString()}
                    </td>
                    <td className="py-3 px-4">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getStatusBadge(d.status)}`}>
                        {d.status}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right font-sans">
                      {hasRole('ADMIN', 'CUSTOM_OFFICIAL') && d.status !== 'APPROVED' ? (
                        <button
                          onClick={() => {
                            setSelectedDec(d);
                            setReviewForm({
                              status: 'APPROVED',
                              reviewedBy: user?.username || 'customs_agent',
                              notes: 'Declaration and Harmonized System tariffs verified compliant.'
                            });
                            setIsReviewOpen(true);
                          }}
                          className="px-2.5 py-1 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-[11px] shadow transition"
                        >
                          Review
                        </button>
                      ) : (
                        <span className="text-[11px] text-gray-400">Processed</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* File Declaration Modal */}
      <Modal
        isOpen={isFileOpen}
        onClose={() => setIsFileOpen(false)}
        title="Electronic Customs Filing (Single Window)"
        subtitle="Submit export/import declaration for automated regulatory screening"
      >
        <form onSubmit={handleFileDeclaration} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Tracking Number</label>
            <input
              type="text"
              placeholder="e.g. TRK-US-2026-0001"
              value={fileForm.trackingNumber}
              onChange={(e) => setFileForm({ ...fileForm, trackingNumber: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Origin Country</label>
              <input
                type="text"
                value={fileForm.originCountry}
                onChange={(e) => setFileForm({ ...fileForm, originCountry: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Destination Country</label>
              <input
                type="text"
                value={fileForm.destinationCountry}
                onChange={(e) => setFileForm({ ...fileForm, destinationCountry: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Cargo Description</label>
            <textarea
              value={fileForm.cargoDescription}
              onChange={(e) => setFileForm({ ...fileForm, cargoDescription: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              rows={2}
              required
            />
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Declared USD</label>
              <input
                type="number"
                step="0.01"
                value={fileForm.declaredValueUSD}
                onChange={(e) => setFileForm({ ...fileForm, declaredValueUSD: parseFloat(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Tariff HS Code</label>
              <input
                type="text"
                value={fileForm.tariffCode}
                onChange={(e) => setFileForm({ ...fileForm, tariffCode: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Duty Fee (USD)</label>
              <input
                type="number"
                step="0.01"
                value={fileForm.dutyFeeUSD}
                onChange={(e) => setFileForm({ ...fileForm, dutyFeeUSD: parseFloat(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsFileOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs text-gray-300 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg"
            >
              Submit Electronic Filing
            </button>
          </div>
        </form>
      </Modal>

      {/* Review Modal */}
      <Modal
        isOpen={isReviewOpen}
        onClose={() => setIsReviewOpen(false)}
        title={`Review Declaration: ${selectedDec?.declarationNumber}`}
        subtitle="Official customs determination and inspection audit"
      >
        <form onSubmit={handleReviewDeclaration} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Adjudication Decision</label>
            <select
              value={reviewForm.status}
              onChange={(e) => setReviewForm({ ...reviewForm, status: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white focus:outline-none focus:border-blue-500"
            >
              <option value="APPROVED">APPROVED (Release Cargo for Dispatch)</option>
              <option value="HOLD">HOLD (Quarantine / Document Audit Required)</option>
              <option value="REJECTED">REJECTED (Regulatory Sanction Violation)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Reviewer Principal</label>
            <input
              type="text"
              value={reviewForm.reviewedBy}
              onChange={(e) => setReviewForm({ ...reviewForm, reviewedBy: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Official Inspection Notes</label>
            <textarea
              value={reviewForm.notes}
              onChange={(e) => setReviewForm({ ...reviewForm, notes: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              rows={3}
              required
            />
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsReviewOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs text-gray-300 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-xs font-bold text-white shadow-lg"
            >
              Commit Decision
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
