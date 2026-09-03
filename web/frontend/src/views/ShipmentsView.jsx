import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import { Modal } from '../components/common/Modal';
import {
  Package,
  Search,
  Filter,
  Plus,
  ArrowRight,
  Clock,
  AlertTriangle,
  CheckCircle2,
  Ship,
  Truck,
  Building2,
  Trash2,
  Edit3,
  ExternalLink
} from 'lucide-react';

export const ShipmentsView = () => {
  const { user, hasRole } = useAuth();
  const { error: toastError, success: toastSuccess } = useToast();

  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [viewMode, setViewMode] = useState('all'); // 'all' or 'delays'

  // Modal States
  const [selectedShipment, setSelectedShipment] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isUpdateStatusOpen, setIsUpdateStatusOpen] = useState(false);

  // New Shipment Form
  const [createForm, setCreateForm] = useState({
    senderUsername: user?.username || 'customer1',
    originCountry: 'USA',
    destinationCountry: 'SGP',
    originHub: 'Port of Los Angeles (USLAX)',
    destinationHub: 'Port of Singapore (SGSIN)',
    carrierName: 'Pacific Express Line',
    hazardous: false,
    weightKg: 150.0,
    declaredValueUSD: 5000.0,
    assignedVendor: 'VND-001',
    items: [
      { itemSku: 'SKU-MED-001', itemName: 'Cold Chain Insulin Vials', quantity: 10, unitPrice: 45.5, weightKg: 2.5 }
    ]
  });

  // Status Update Form
  const [updateStatusForm, setUpdateStatusForm] = useState({
    trackingNumber: '',
    status: 'IN_TRANSIT',
    updatedBy: user?.username || 'admin'
  });

  const loadShipments = async () => {
    setLoading(true);
    try {
      if (viewMode === 'delays') {
        const data = await api.shipments.getDelays();
        setShipments(data || []);
      } else if (user?.role === 'CUSTOMER') {
        const data = await api.shipments.getBySender(user.username);
        setShipments(data || []);
      } else {
        const data = await api.shipments.getAll();
        setShipments(data || []);
      }
    } catch (err) {
      toastError('Failed to fetch shipments: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadShipments();
  }, [viewMode]);

  const handleCreateShipment = async (e) => {
    e.preventDefault();
    try {
      const created = await api.shipments.create(createForm);
      toastSuccess(`Shipment created with Tracking #${created.trackingNumber}`);
      setIsCreateOpen(false);
      loadShipments();
    } catch (err) {
      toastError('Failed to create shipment: ' + err.message);
    }
  };

  const handleUpdateStatus = async (e) => {
    e.preventDefault();
    try {
      await api.shipments.updateStatus(
        updateStatusForm.trackingNumber,
        updateStatusForm.status,
        updateStatusForm.updatedBy
      );
      toastSuccess(`Status updated to ${updateStatusForm.status}`);
      setIsUpdateStatusOpen(false);
      loadShipments();
    } catch (err) {
      toastError('Failed to update status: ' + err.message);
    }
  };

  const addItemToCreateForm = () => {
    setCreateForm({
      ...createForm,
      items: [
        ...createForm.items,
        { itemSku: 'SKU-ELEC-002', itemName: 'Industrial Sensor Nodes', quantity: 5, unitPrice: 89.0, weightKg: 1.0 }
      ]
    });
  };

  const removeItemFromCreateForm = (idx) => {
    setCreateForm({
      ...createForm,
      items: createForm.items.filter((_, i) => i !== idx)
    });
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'DELIVERED':
        return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40';
      case 'IN_TRANSIT':
      case 'OUT_FOR_DELIVERY':
        return 'bg-blue-500/20 text-blue-300 border-blue-500/40';
      case 'CUSTOMS_HOLD':
        return 'bg-rose-500/20 text-rose-300 border-rose-500/40 animate-pulse';
      case 'CANCELLED':
      case 'RETURNED':
        return 'bg-gray-500/20 text-gray-400 border-gray-500/40';
      case 'CREATED':
      default:
        return 'bg-amber-500/20 text-amber-300 border-amber-500/40';
    }
  };

  const filteredShipments = shipments.filter((s) => {
    const matchesSearch =
      (s.trackingNumber && s.trackingNumber.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (s.senderUsername && s.senderUsername.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (s.originCountry && s.originCountry.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (s.destinationCountry && s.destinationCountry.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesStatus = statusFilter === 'ALL' || s.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Header & Action Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-white tracking-tight">Shipment Registry & Live Tracking</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            Real-time status progression, customs checkpoint tracking, and delay risk telemetry.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <button
            onClick={() => setViewMode(viewMode === 'all' ? 'delays' : 'all')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl border text-xs font-semibold transition ${
              viewMode === 'delays'
                ? 'bg-amber-500/20 border-amber-500/50 text-amber-300'
                : 'bg-gray-800/80 border-gray-700 text-gray-300 hover:bg-gray-700'
            }`}
          >
            <Clock className="w-3.5 h-3.5 text-amber-400" />
            <span>{viewMode === 'delays' ? 'Showing Delays Only' : 'Filter Delays'}</span>
          </button>

          {hasRole('ADMIN', 'LOGISTIC_PERSONNEL', 'CUSTOMER') && (
            <button
              onClick={() => setIsCreateOpen(true)}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20 transition"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Create Shipment</span>
            </button>
          )}
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col md:flex-row items-center gap-3 bg-[#121929] p-3 rounded-2xl border border-gray-800">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-gray-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by Tracking #, Sender, Origin, or Destination..."
            className="w-full bg-[#0d1424] border border-gray-700/80 rounded-xl py-2 pl-10 pr-4 text-xs text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
          />
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto">
          <Filter className="w-4 h-4 text-gray-400" />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="bg-[#0d1424] border border-gray-700/80 rounded-xl py-2 px-3 text-xs text-gray-300 focus:outline-none focus:border-blue-500"
          >
            <option value="ALL">All Statuses</option>
            <option value="CREATED">CREATED</option>
            <option value="IN_TRANSIT">IN_TRANSIT</option>
            <option value="CUSTOMS_HOLD">CUSTOMS_HOLD</option>
            <option value="OUT_FOR_DELIVERY">OUT_FOR_DELIVERY</option>
            <option value="DELIVERED">DELIVERED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>
      </div>

      {/* Shipments Table */}
      <div className="bg-[#121929] border border-gray-800 rounded-3xl overflow-hidden shadow-xl">
        {loading ? (
          <div className="py-16 text-center text-xs text-gray-400">Loading shipments...</div>
        ) : filteredShipments.length === 0 ? (
          <div className="py-16 text-center text-xs text-gray-400">
            No shipments found matching criteria.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#0d1424] border-b border-gray-800 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <tr>
                  <th className="py-3.5 px-4">Tracking Number</th>
                  <th className="py-3.5 px-4">Route</th>
                  <th className="py-3.5 px-4">Sender</th>
                  <th className="py-3.5 px-4">Carrier / Vendor</th>
                  <th className="py-3.5 px-4">Declared Value</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {filteredShipments.map((s) => (
                  <tr key={s.id || s.trackingNumber} className="hover:bg-gray-800/30 transition">
                    <td className="py-3 px-4 font-mono font-bold text-blue-400">
                      {s.trackingNumber}
                    </td>
                    <td className="py-3 px-4 font-medium text-gray-300">
                      <div className="flex items-center gap-1.5">
                        <span>{s.originCountry}</span>
                        <ArrowRight className="w-3 h-3 text-gray-500" />
                        <span>{s.destinationCountry}</span>
                      </div>
                      <div className="text-[10px] text-gray-400 font-mono mt-0.5">
                        {s.weightKg} kg &bull; {s.hazardous ? '⚠️ Hazardous' : 'Standard'}
                      </div>
                    </td>
                    <td className="py-3 px-4 text-gray-300">{s.senderUsername}</td>
                    <td className="py-3 px-4">
                      <div className="font-medium text-gray-200">{s.carrierName || 'Unassigned'}</div>
                      {s.assignedVendor && (
                        <div className="text-[10px] text-cyan-400 font-mono">
                          Vendor: {s.assignedVendor}
                        </div>
                      )}
                    </td>
                    <td className="py-3 px-4 font-mono font-semibold text-emerald-400">
                      ${s.declaredValueUSD ? s.declaredValueUSD.toLocaleString(undefined, { minimumFractionDigits: 2 }) : '0.00'}
                    </td>
                    <td className="py-3 px-4">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getStatusBadge(s.status)}`}>
                        {s.status}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right space-x-1.5">
                      <button
                        onClick={() => {
                          setSelectedShipment(s);
                          setIsDetailOpen(true);
                        }}
                        className="p-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white transition"
                        title="View Details"
                      >
                        <ExternalLink className="w-3.5 h-3.5" />
                      </button>

                      {hasRole('ADMIN', 'LOGISTIC_PERSONNEL') && (
                        <button
                          onClick={() => {
                            setUpdateStatusForm({
                              trackingNumber: s.trackingNumber,
                              status: s.status,
                              updatedBy: user?.username || 'admin'
                            });
                            setIsUpdateStatusOpen(true);
                          }}
                          className="p-1.5 rounded-lg bg-blue-900/40 hover:bg-blue-800/60 text-blue-300 border border-blue-500/30 transition"
                          title="Update Status"
                        >
                          <Edit3 className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Detail & Progress Modal */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        title={`Shipment Details: ${selectedShipment?.trackingNumber}`}
        subtitle="End-to-end multimodal tracking and manifest items"
        maxWidth="max-w-3xl"
      >
        {selectedShipment && (
          <div className="space-y-6">
            {/* Status Stepper */}
            <div className="bg-[#111827] p-5 rounded-2xl border border-gray-800">
              <div className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-4">
                Lifecycle Progression
              </div>
              <div className="flex items-center justify-between relative">
                {['CREATED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED'].map((step, idx) => {
                  const currentIdx = ['CREATED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED'].indexOf(selectedShipment.status);
                  const isDone = currentIdx >= idx;
                  const isCurrent = selectedShipment.status === step;

                  return (
                    <div key={step} className="flex flex-col items-center gap-1.5 relative z-10 flex-1">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs border-2 transition ${
                        isCurrent
                          ? 'bg-blue-600 border-blue-400 text-white shadow-lg shadow-blue-500/50'
                          : isDone
                          ? 'bg-emerald-600 border-emerald-400 text-white'
                          : 'bg-gray-800 border-gray-700 text-gray-400'
                      }`}>
                        {idx + 1}
                      </div>
                      <span className={`text-[10px] font-bold text-center ${isCurrent ? 'text-blue-300' : isDone ? 'text-emerald-300' : 'text-gray-400'}`}>
                        {step.replace('_', ' ')}
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Shipment Attributes Grid */}
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3 text-xs">
              <div className="bg-[#111827] p-3 rounded-xl border border-gray-800">
                <span className="text-gray-400 block text-[10px] uppercase font-semibold">Origin Hub</span>
                <span className="font-bold text-white mt-0.5 block">{selectedShipment.originHub || selectedShipment.originCountry}</span>
              </div>
              <div className="bg-[#111827] p-3 rounded-xl border border-gray-800">
                <span className="text-gray-400 block text-[10px] uppercase font-semibold">Destination Hub</span>
                <span className="font-bold text-white mt-0.5 block">{selectedShipment.destinationHub || selectedShipment.destinationCountry}</span>
              </div>
              <div className="bg-[#111827] p-3 rounded-xl border border-gray-800">
                <span className="text-gray-400 block text-[10px] uppercase font-semibold">Carrier / Vendor</span>
                <span className="font-bold text-white mt-0.5 block">{selectedShipment.carrierName || 'Maersk'} ({selectedShipment.assignedVendor || 'N/A'})</span>
              </div>
              <div className="bg-[#111827] p-3 rounded-xl border border-gray-800">
                <span className="text-gray-400 block text-[10px] uppercase font-semibold">Total Weight</span>
                <span className="font-bold text-white mt-0.5 block">{selectedShipment.weightKg} kg</span>
              </div>
              <div className="bg-[#111827] p-3 rounded-xl border border-gray-800">
                <span className="text-gray-400 block text-[10px] uppercase font-semibold">Declared Value</span>
                <span className="font-bold text-emerald-400 mt-0.5 block">${selectedShipment.declaredValueUSD?.toLocaleString()} USD</span>
              </div>
              <div className="bg-[#111827] p-3 rounded-xl border border-gray-800">
                <span className="text-gray-400 block text-[10px] uppercase font-semibold">Hazardous Cargo</span>
                <span className={`font-bold mt-0.5 block ${selectedShipment.hazardous ? 'text-rose-400' : 'text-gray-300'}`}>
                  {selectedShipment.hazardous ? 'YES (HazMat Enforced)' : 'NO'}
                </span>
              </div>
            </div>

            {/* Line Items Table */}
            <div>
              <h4 className="text-xs font-bold text-gray-300 uppercase tracking-wider mb-2">
                Manifest Line Items
              </h4>
              <div className="bg-[#111827] border border-gray-800 rounded-xl overflow-hidden">
                <table className="w-full text-left text-xs">
                  <thead className="bg-gray-900 text-[10px] font-bold text-gray-400 uppercase">
                    <tr>
                      <th className="p-2.5">SKU</th>
                      <th className="p-2.5">Description</th>
                      <th className="p-2.5">Qty</th>
                      <th className="p-2.5">Unit Price</th>
                      <th className="p-2.5 text-right">Line Total</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-800 font-mono text-[11px]">
                    {selectedShipment.items && selectedShipment.items.length > 0 ? (
                      selectedShipment.items.map((it, idx) => (
                        <tr key={idx}>
                          <td className="p-2.5 text-blue-400 font-bold">{it.itemSku}</td>
                          <td className="p-2.5 text-gray-300">{it.itemName}</td>
                          <td className="p-2.5 text-gray-300">{it.quantity}</td>
                          <td className="p-2.5 text-gray-300">${it.unitPrice}</td>
                          <td className="p-2.5 text-right text-emerald-400 font-bold">
                            ${(it.quantity * it.unitPrice).toFixed(2)}
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={5} className="p-4 text-center text-gray-400">
                          Single consignment cargo item.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </Modal>

      {/* Create Shipment Modal */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Direct Shipment Creation"
        subtitle="Initiate and persist a new cross-border consignment in the tracking registry"
        maxWidth="max-w-3xl"
      >
        <form onSubmit={handleCreateShipment} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Sender Username</label>
              <input
                type="text"
                value={createForm.senderUsername}
                onChange={(e) => setCreateForm({ ...createForm, senderUsername: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Carrier Name</label>
              <input
                type="text"
                value={createForm.carrierName}
                onChange={(e) => setCreateForm({ ...createForm, carrierName: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Origin Country</label>
              <input
                type="text"
                value={createForm.originCountry}
                onChange={(e) => setCreateForm({ ...createForm, originCountry: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Destination Country</label>
              <input
                type="text"
                value={createForm.destinationCountry}
                onChange={(e) => setCreateForm({ ...createForm, destinationCountry: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Weight (kg)</label>
              <input
                type="number"
                step="0.1"
                value={createForm.weightKg}
                onChange={(e) => setCreateForm({ ...createForm, weightKg: parseFloat(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Declared Value (USD)</label>
              <input
                type="number"
                step="0.01"
                value={createForm.declaredValueUSD}
                onChange={(e) => setCreateForm({ ...createForm, declaredValueUSD: parseFloat(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Assigned Vendor</label>
              <input
                type="text"
                value={createForm.assignedVendor}
                onChange={(e) => setCreateForm({ ...createForm, assignedVendor: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              />
            </div>
          </div>

          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="hazardCheck"
              checked={createForm.hazardous}
              onChange={(e) => setCreateForm({ ...createForm, hazardous: e.target.checked })}
              className="w-4 h-4 rounded text-blue-600 bg-gray-900 border-gray-700"
            />
            <label htmlFor="hazardCheck" className="text-xs font-medium text-gray-300">
              Hazardous Cargo Flag (Enforces HazMat handling protocols)
            </label>
          </div>

          {/* Dynamic Line Items */}
          <div className="pt-4 border-t border-gray-800">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-bold text-gray-300 uppercase">Consignment Line Items</span>
              <button
                type="button"
                onClick={addItemToCreateForm}
                className="text-xs text-blue-400 hover:text-blue-300 font-semibold flex items-center gap-1"
              >
                <Plus className="w-3.5 h-3.5" />
                Add Item
              </button>
            </div>

            <div className="space-y-2 max-h-40 overflow-y-auto custom-scrollbar">
              {createForm.items.map((item, idx) => (
                <div key={idx} className="flex items-center gap-2 bg-[#111827] p-2 rounded-xl border border-gray-800 text-xs">
                  <input
                    type="text"
                    placeholder="SKU"
                    value={item.itemSku}
                    onChange={(e) => {
                      const newItems = [...createForm.items];
                      newItems[idx].itemSku = e.target.value;
                      setCreateForm({ ...createForm, items: newItems });
                    }}
                    className="w-28 bg-[#0d1424] border border-gray-700 rounded-lg p-1.5 text-xs text-white font-mono"
                  />
                  <input
                    type="text"
                    placeholder="Name"
                    value={item.itemName}
                    onChange={(e) => {
                      const newItems = [...createForm.items];
                      newItems[idx].itemName = e.target.value;
                      setCreateForm({ ...createForm, items: newItems });
                    }}
                    className="flex-1 bg-[#0d1424] border border-gray-700 rounded-lg p-1.5 text-xs text-white"
                  />
                  <input
                    type="number"
                    placeholder="Qty"
                    value={item.quantity}
                    onChange={(e) => {
                      const newItems = [...createForm.items];
                      newItems[idx].quantity = parseInt(e.target.value) || 1;
                      setCreateForm({ ...createForm, items: newItems });
                    }}
                    className="w-16 bg-[#0d1424] border border-gray-700 rounded-lg p-1.5 text-xs text-white text-right"
                  />
                  <input
                    type="number"
                    step="0.01"
                    placeholder="Unit $"
                    value={item.unitPrice}
                    onChange={(e) => {
                      const newItems = [...createForm.items];
                      newItems[idx].unitPrice = parseFloat(e.target.value) || 0;
                      setCreateForm({ ...createForm, items: newItems });
                    }}
                    className="w-20 bg-[#0d1424] border border-gray-700 rounded-lg p-1.5 text-xs text-white text-right"
                  />
                  <button
                    type="button"
                    onClick={() => removeItemFromCreateForm(idx)}
                    className="p-1 text-gray-400 hover:text-rose-400 transition"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              ))}
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsCreateOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 hover:bg-gray-700 text-xs font-semibold text-gray-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20"
            >
              Confirm & Save Shipment
            </button>
          </div>
        </form>
      </Modal>

      {/* Update Status Modal */}
      <Modal
        isOpen={isUpdateStatusOpen}
        onClose={() => setIsUpdateStatusOpen(false)}
        title="Update Shipment Lifecycle Status"
        subtitle={`Modify state for tracking #${updateStatusForm.trackingNumber}`}
      >
        <form onSubmit={handleUpdateStatus} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Select New Status</label>
            <select
              value={updateStatusForm.status}
              onChange={(e) => setUpdateStatusForm({ ...updateStatusForm, status: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white focus:outline-none focus:border-blue-500"
            >
              <option value="CREATED">CREATED</option>
              <option value="IN_TRANSIT">IN_TRANSIT</option>
              <option value="CUSTOMS_HOLD">CUSTOMS_HOLD</option>
              <option value="OUT_FOR_DELIVERY">OUT_FOR_DELIVERY</option>
              <option value="DELIVERED">DELIVERED</option>
              <option value="CANCELLED">CANCELLED</option>
              <option value="RETURNED">RETURNED</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Updated By Principal</label>
            <input
              type="text"
              value={updateStatusForm.updatedBy}
              onChange={(e) => setUpdateStatusForm({ ...updateStatusForm, updatedBy: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
              required
            />
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsUpdateStatusOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs font-semibold text-gray-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20"
            >
              Commit Status Change
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
