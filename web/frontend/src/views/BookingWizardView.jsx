import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import {
  Sparkles,
  MapPin,
  Package,
  Truck,
  CheckCircle2,
  Trash2,
  Plus,
  ArrowRight,
  ArrowLeft,
  XCircle,
  Calculator,
  ShieldCheck,
  Ship
} from 'lucide-react';

export const BookingWizardView = ({ onShipmentCreated }) => {
  const { user } = useAuth();
  const { error: toastError, success: toastSuccess, info: toastInfo } = useToast();

  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [sessionActive, setSessionActive] = useState(false);
  const [confirmedShipment, setConfirmedShipment] = useState(null);

  // Step 1: Initiation
  const [startForm, setStartForm] = useState({
    senderUsername: user?.username || 'customer1',
    origin: 'Port of Los Angeles (USLAX)',
    destination: 'Port of Singapore (SGSIN)'
  });

  // Step 2: Add Items
  const [items, setItems] = useState([]);
  const [itemInput, setItemInput] = useState({
    sku: 'SKU-MED-001',
    description: 'Cold Chain Insulin Vials',
    quantity: 50,
    weightKg: 12.5,
    declaredValue: 2275.0
  });

  // Step 3: Carrier Selection
  const [carrierForm, setCarrierForm] = useState({
    carrierCode: 'MAERSK-SEA',
    serviceLevel: 'EXPRESS'
  });

  // Step 4: Live Summary
  const [summary, setSummary] = useState(null);

  // Start Session (Step 1 -> Step 2)
  const handleStartBooking = async (e) => {
    e?.preventDefault();
    setLoading(true);
    try {
      await api.booking.start(startForm.senderUsername, startForm.origin, startForm.destination);
      setSessionActive(true);
      setStep(2);
      toastSuccess('Stateful booking session initialized in EJB container.');
    } catch (err) {
      toastError('Failed to start session: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // Add Item to Stateful Cart (Step 2)
  const handleAddItem = async (e) => {
    e?.preventDefault();
    if (!itemInput.sku || itemInput.quantity <= 0) {
      toastError('Please enter valid SKU and quantity');
      return;
    }

    setLoading(true);
    try {
      await api.booking.addItem(
        itemInput.sku,
        itemInput.description,
        itemInput.quantity,
        itemInput.weightKg,
        itemInput.declaredValue
      );
      setItems([...items, { ...itemInput }]);
      toastSuccess(`Added ${itemInput.sku} to conversational booking cart.`);
      // Reset input SKU
      setItemInput({
        sku: '',
        description: '',
        quantity: 1,
        weightKg: 1.0,
        declaredValue: 100.0
      });
    } catch (err) {
      toastError('Failed to add item: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // Remove Item from Stateful Cart (Step 2)
  const handleRemoveItem = async (sku) => {
    try {
      await api.booking.removeItem(sku);
      setItems(items.filter((it) => it.sku !== sku));
      toastInfo(`Removed ${sku} from cart.`);
    } catch (err) {
      toastError('Failed to remove item: ' + err.message);
    }
  };

  // Select Carrier (Step 3 -> Step 4)
  const handleSelectCarrier = async (e) => {
    e?.preventDefault();
    setLoading(true);
    try {
      await api.booking.selectCarrier(carrierForm.carrierCode, carrierForm.serviceLevel);
      // Fetch live summary from Stateful EJB
      const sum = await api.booking.getSummary();
      setSummary(sum);
      setStep(4);
      toastSuccess('Carrier preference recorded. Estimated rates calculated.');
    } catch (err) {
      toastError('Failed to set carrier: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // Confirm Final Booking (Step 4)
  const handleConfirm = async () => {
    setLoading(true);
    try {
      const result = await api.booking.confirm();
      setConfirmedShipment(result);
      setSessionActive(false);
      setStep(5);
      toastSuccess('Stateful booking successfully committed into tracking registry!');
      if (onShipmentCreated) onShipmentCreated(result);
    } catch (err) {
      toastError('Confirmation failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // Cancel Session (@Remove)
  const handleCancel = async () => {
    try {
      await api.booking.cancel();
      setSessionActive(false);
      setStep(1);
      setItems([]);
      setSummary(null);
      toastInfo('Conversational booking session discarded.');
    } catch (err) {
      toastError('Cancel failed: ' + err.message);
    }
  };

  const resetWizard = () => {
    setStep(1);
    setConfirmedShipment(null);
    setSessionActive(false);
    setItems([]);
    setSummary(null);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Wizard Header Banner */}
      <div className="bg-gradient-to-r from-blue-900/40 via-indigo-900/30 to-purple-900/20 border border-blue-500/30 p-6 rounded-3xl shadow-xl flex items-center justify-between">
        <div>
          <div className="flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-blue-400" />
            <h1 className="text-xl font-black text-white tracking-tight">Stateful Shipment Booking Session</h1>
          </div>
          <p className="text-xs text-blue-200/70 mt-1">
            Leveraging Jakarta EE Stateful Session Beans (<code>@Stateful</code>) with client-isolated conversational cart state.
          </p>
        </div>

        {sessionActive && (
          <button
            onClick={handleCancel}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/30 text-xs font-semibold transition"
          >
            <XCircle className="w-4 h-4" />
            <span>Discard Session</span>
          </button>
        )}
      </div>

      {/* Stepper Indicator */}
      <div className="bg-[#121929] border border-gray-800 p-4 rounded-2xl shadow-md">
        <div className="flex items-center justify-between max-w-2xl mx-auto relative">
          {[
            { num: 1, label: 'Origin & Dest' },
            { num: 2, label: 'Cargo Items' },
            { num: 3, label: 'Carrier Select' },
            { num: 4, label: 'Review & Cost' }
          ].map((s) => {
            const isDone = step > s.num;
            const isCurrent = step === s.num;
            return (
              <div key={s.num} className="flex flex-col items-center gap-1 relative z-10">
                <div
                  className={`w-9 h-9 rounded-full flex items-center justify-center font-bold text-xs border-2 transition-all ${
                    isCurrent
                      ? 'bg-blue-600 border-blue-400 text-white shadow-lg shadow-blue-500/50 scale-110'
                      : isDone
                      ? 'bg-emerald-600 border-emerald-400 text-white'
                      : 'bg-gray-800 border-gray-700 text-gray-400'
                  }`}
                >
                  {isDone ? <CheckCircle2 className="w-4 h-4" /> : s.num}
                </div>
                <span className={`text-[11px] font-bold ${isCurrent ? 'text-blue-300' : isDone ? 'text-emerald-300' : 'text-gray-400'}`}>
                  {s.label}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Step 1: Origin & Destination */}
      {step === 1 && (
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl animate-fade-in">
          <h2 className="text-base font-bold text-white mb-1">Step 1: Initiate Booking Session</h2>
          <p className="text-xs text-gray-400 mb-6">Enter principal sender and international transit hubs.</p>

          <form onSubmit={handleStartBooking} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Sender Username</label>
              <input
                type="text"
                value={startForm.senderUsername}
                onChange={(e) => setStartForm({ ...startForm, senderUsername: e.target.value })}
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white"
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Origin Port / Hub</label>
                <input
                  type="text"
                  value={startForm.origin}
                  onChange={(e) => setStartForm({ ...startForm, origin: e.target.value })}
                  placeholder="e.g. Port of Los Angeles (USLAX)"
                  className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Destination Port / Hub</label>
                <input
                  type="text"
                  value={startForm.destination}
                  onChange={(e) => setStartForm({ ...startForm, destination: e.target.value })}
                  placeholder="e.g. Port of Singapore (SGSIN)"
                  className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white"
                  required
                />
              </div>
            </div>

            <div className="flex justify-end pt-4">
              <button
                type="submit"
                disabled={loading}
                className="flex items-center gap-2 px-6 py-3 rounded-xl bg-blue-600 hover:bg-blue-500 text-sm font-bold text-white shadow-lg shadow-blue-500/25 transition"
              >
                <span>Proceed to Cargo Cart</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Step 2: Add Line Items to Stateful Cart */}
      {step === 2 && (
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl animate-fade-in space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-white">Step 2: Add Cargo Items</h2>
              <p className="text-xs text-gray-400">Items are stored conversationally within your EJB session bean cart.</p>
            </div>
            <div className="text-xs font-mono text-cyan-400 bg-cyan-950/40 border border-cyan-500/30 px-3 py-1 rounded-lg">
              Route: {startForm.origin} &rarr; {startForm.destination}
            </div>
          </div>

          {/* New Item Form */}
          <form onSubmit={handleAddItem} className="bg-[#0d1424] p-4 rounded-2xl border border-gray-800 space-y-3">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-gray-400 uppercase mb-1">SKU</label>
                <input
                  type="text"
                  placeholder="SKU-AUTO-003"
                  value={itemInput.sku}
                  onChange={(e) => setItemInput({ ...itemInput, sku: e.target.value })}
                  className="w-full bg-[#161f30] border border-gray-700 rounded-lg p-2 text-xs text-white font-mono"
                  required
                />
              </div>
              <div className="md:col-span-2">
                <label className="block text-[11px] font-semibold text-gray-400 uppercase mb-1">Description</label>
                <input
                  type="text"
                  placeholder="Cargo description..."
                  value={itemInput.description}
                  onChange={(e) => setItemInput({ ...itemInput, description: e.target.value })}
                  className="w-full bg-[#161f30] border border-gray-700 rounded-lg p-2 text-xs text-white"
                  required
                />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-gray-400 uppercase mb-1">Quantity</label>
                <input
                  type="number"
                  min="1"
                  value={itemInput.quantity}
                  onChange={(e) => setItemInput({ ...itemInput, quantity: parseInt(e.target.value) || 1 })}
                  className="w-full bg-[#161f30] border border-gray-700 rounded-lg p-2 text-xs text-white"
                  required
                />
              </div>
              <div>
                <label className="block text-[11px] font-semibold text-gray-400 uppercase mb-1">Weight (kg)</label>
                <input
                  type="number"
                  step="0.1"
                  value={itemInput.weightKg}
                  onChange={(e) => setItemInput({ ...itemInput, weightKg: parseFloat(e.target.value) || 0 })}
                  className="w-full bg-[#161f30] border border-gray-700 rounded-lg p-2 text-xs text-white"
                  required
                />
              </div>
              <div>
                <label className="block text-[11px] font-semibold text-gray-400 uppercase mb-1">Declared Value ($)</label>
                <input
                  type="number"
                  step="0.01"
                  value={itemInput.declaredValue}
                  onChange={(e) => setItemInput({ ...itemInput, declaredValue: parseFloat(e.target.value) || 0 })}
                  className="w-full bg-[#161f30] border border-gray-700 rounded-lg p-2 text-xs text-white"
                  required
                />
              </div>
            </div>

            <div className="flex justify-end">
              <button
                type="submit"
                disabled={loading}
                className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow transition"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Add Item to Cart</span>
              </button>
            </div>
          </form>

          {/* Current Cart Table */}
          <div>
            <h3 className="text-xs font-bold text-gray-300 uppercase tracking-wider mb-2">
              Cart Items ({items.length})
            </h3>
            {items.length === 0 ? (
              <div className="p-8 text-center bg-[#0d1424] rounded-2xl border border-dashed border-gray-800 text-xs text-gray-400">
                No items added yet. Add at least one item above to continue.
              </div>
            ) : (
              <div className="bg-[#0d1424] border border-gray-800 rounded-2xl overflow-hidden">
                <table className="w-full text-left text-xs">
                  <thead className="bg-gray-900/80 text-[10px] uppercase font-bold text-gray-400">
                    <tr>
                      <th className="p-3">SKU</th>
                      <th className="p-3">Description</th>
                      <th className="p-3">Qty</th>
                      <th className="p-3">Weight</th>
                      <th className="p-3">Declared Value</th>
                      <th className="p-3 text-right">Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-800 font-mono text-[11px]">
                    {items.map((it) => (
                      <tr key={it.sku} className="hover:bg-gray-800/40">
                        <td className="p-3 text-blue-400 font-bold">{it.sku}</td>
                        <td className="p-3 text-gray-300">{it.description}</td>
                        <td className="p-3 text-gray-300">{it.quantity}</td>
                        <td className="p-3 text-gray-300">{it.weightKg} kg</td>
                        <td className="p-3 text-emerald-400 font-bold">${it.declaredValue}</td>
                        <td className="p-3 text-right">
                          <button
                            onClick={() => handleRemoveItem(it.sku)}
                            className="p-1 text-gray-400 hover:text-rose-400 transition"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          <div className="flex justify-between items-center pt-4 border-t border-gray-800">
            <button
              onClick={() => setStep(1)}
              className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-xs font-semibold text-gray-300"
            >
              <ArrowLeft className="w-4 h-4" />
              <span>Back</span>
            </button>
            <button
              onClick={() => setStep(3)}
              disabled={items.length === 0}
              className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20 disabled:opacity-40"
            >
              <span>Choose Carrier & Service</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Carrier & Service Selection */}
      {step === 3 && (
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl animate-fade-in space-y-6">
          <div>
            <h2 className="text-base font-bold text-white">Step 3: Carrier & Service Selection</h2>
            <p className="text-xs text-gray-400">Select premier logistics carrier network and service tier.</p>
          </div>

          <form onSubmit={handleSelectCarrier} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Carrier Partner</label>
                <select
                  value={carrierForm.carrierCode}
                  onChange={(e) => setCarrierForm({ ...carrierForm, carrierCode: e.target.value })}
                  className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white focus:outline-none focus:border-blue-500"
                >
                  <option value="MAERSK-SEA">Maersk Line (Ocean Freight)</option>
                  <option value="FEDEX-AIR">FedEx Express (Air Freight)</option>
                  <option value="DHL-GLOBAL">DHL Global Forwarding (Multimodal)</option>
                  <option value="MSC-LOGISTICS">MSC Mediterranean Shipping (Ocean)</option>
                  <option value="TRANS-RAIL-01">TransEurasian Rail Cargo</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">Service Level</label>
                <select
                  value={carrierForm.serviceLevel}
                  onChange={(e) => setCarrierForm({ ...carrierForm, serviceLevel: e.target.value })}
                  className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-sm text-white focus:outline-none focus:border-blue-500"
                >
                  <option value="EXPRESS">Express Priority (1-3 Days, +30% Surcharge)</option>
                  <option value="STANDARD">Standard Commercial (5-10 Days)</option>
                  <option value="ECONOMY">Economy Bulk Freight (12-25 Days, Best Rate)</option>
                  <option value="OVERNIGHT">Overnight Air Critical</option>
                </select>
              </div>
            </div>

            <div className="flex justify-between items-center pt-6 border-t border-gray-800">
              <button
                type="button"
                onClick={() => setStep(2)}
                className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-xs font-semibold text-gray-300"
              >
                <ArrowLeft className="w-4 h-4" />
                <span>Back</span>
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20"
              >
                <span>Calculate & Review Summary</span>
                <Calculator className="w-4 h-4" />
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Step 4: Summary & Final Confirmation */}
      {step === 4 && summary && (
        <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl animate-fade-in space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-white">Step 4: Real-time Tariff & Summary Review</h2>
              <p className="text-xs text-gray-400">Review calculated tariff rates and dispatch verification.</p>
            </div>
            <span className="text-xs px-2.5 py-1 rounded-full bg-blue-500/20 text-blue-300 border border-blue-500/30 font-mono font-bold">
              Ready for Commitment
            </span>
          </div>

          {/* Pricing Highlight Card */}
          <div className="bg-gradient-to-br from-[#152238] to-[#101726] border border-blue-500/30 rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <span className="text-[11px] uppercase font-bold text-blue-400 tracking-wider">Estimated Total Tariff</span>
              <div className="text-3xl font-black text-white mt-1">
                ${summary.estimatedCostUSD?.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })} <span className="text-sm font-semibold text-gray-400">USD</span>
              </div>
              <div className="text-xs text-gray-400 mt-1">
                Carrier: <span className="text-gray-200 font-semibold">{summary.carrierCode}</span> &bull; Service: <span className="text-gray-200 font-semibold">{summary.serviceLevel}</span>
              </div>
            </div>

            <div className="flex gap-4 text-xs font-mono border-t sm:border-t-0 sm:border-l border-gray-700/60 pt-3 sm:pt-0 sm:pl-6">
              <div>
                <span className="text-gray-400 block text-[10px] uppercase font-bold">Total Weight</span>
                <span className="text-base font-bold text-white mt-0.5 block">{summary.totalWeightKg} kg</span>
              </div>
              <div>
                <span className="text-gray-400 block text-[10px] uppercase font-bold">Total Value</span>
                <span className="text-base font-bold text-emerald-400 mt-0.5 block">${summary.totalDeclaredValue?.toLocaleString()} USD</span>
              </div>
            </div>
          </div>

          {/* Items Breakdown */}
          <div className="space-y-2">
            <h4 className="text-xs font-bold text-gray-300 uppercase tracking-wider">
              Enclosed Manifest Items ({summary.items?.length || 0})
            </h4>
            <div className="bg-[#0d1424] border border-gray-800 rounded-2xl overflow-hidden">
              <table className="w-full text-left text-xs">
                <thead className="bg-gray-900/80 text-[10px] uppercase font-bold text-gray-400">
                  <tr>
                    <th className="p-3">SKU</th>
                    <th className="p-3">Description</th>
                    <th className="p-3">Qty</th>
                    <th className="p-3">Weight (kg)</th>
                    <th className="p-3 text-right">Declared ($)</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800 font-mono text-[11px]">
                  {summary.items?.map((it, idx) => (
                    <tr key={idx}>
                      <td className="p-3 text-blue-400 font-bold">{it.sku}</td>
                      <td className="p-3 text-gray-300">{it.description}</td>
                      <td className="p-3 text-gray-300">{it.quantity}</td>
                      <td className="p-3 text-gray-300">{it.weightKg}</td>
                      <td className="p-3 text-right text-emerald-400 font-bold">${it.declaredValue}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="flex justify-between items-center pt-4 border-t border-gray-800">
            <button
              onClick={() => setStep(3)}
              className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-xs font-semibold text-gray-300"
            >
              <ArrowLeft className="w-4 h-4" />
              <span>Back</span>
            </button>
            <div className="flex items-center gap-2">
              <button
                onClick={handleCancel}
                className="px-4 py-2.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/30 text-xs font-semibold"
              >
                Discard Session
              </button>
              <button
                onClick={handleConfirm}
                disabled={loading}
                className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-xs font-bold text-white shadow-lg shadow-emerald-500/20"
              >
                <CheckCircle2 className="w-4 h-4" />
                <span>Confirm & Create Shipment</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Step 5: Success Screen */}
      {step === 5 && confirmedShipment && (
        <div className="bg-[#121929] border border-emerald-500/40 rounded-3xl p-8 shadow-2xl text-center space-y-6 animate-scale-up">
          <div className="w-16 h-16 mx-auto rounded-full bg-emerald-500/20 border-2 border-emerald-500 flex items-center justify-center text-emerald-400 shadow-xl shadow-emerald-500/20">
            <CheckCircle2 className="w-8 h-8" />
          </div>

          <div>
            <h2 className="text-2xl font-black text-white">Booking Successfully Confirmed!</h2>
            <p className="text-xs text-gray-400 mt-1">
              Your stateful session has committed the shipment to persistent JPA storage.
            </p>
          </div>

          <div className="max-w-md mx-auto p-4 bg-[#0d1424] border border-gray-800 rounded-2xl text-left space-y-2 font-mono text-xs">
            <div className="flex justify-between">
              <span className="text-gray-400">Generated Tracking #:</span>
              <span className="text-blue-400 font-bold">{confirmedShipment.trackingNumber}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Origin Hub:</span>
              <span className="text-gray-200">{confirmedShipment.originHub}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Destination Hub:</span>
              <span className="text-gray-200">{confirmedShipment.destinationHub}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Carrier:</span>
              <span className="text-gray-200">{confirmedShipment.carrierName}</span>
            </div>
          </div>

          <div className="flex justify-center gap-3 pt-2">
            <button
              onClick={resetWizard}
              className="px-6 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg transition"
            >
              Start Another Booking
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
