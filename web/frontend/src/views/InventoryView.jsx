import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import { Modal } from '../components/common/Modal';
import {
  Boxes,
  Plus,
  Search,
  AlertTriangle,
  RefreshCw,
  Trash2,
  Building,
  Package,
  Layers
} from 'lucide-react';

export const InventoryView = () => {
  const { user, hasRole } = useAuth();
  const { error: toastError, success: toastSuccess } = useToast();

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showLowStockOnly, setShowLowStockOnly] = useState(false);

  // Modals
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isRestockOpen, setIsRestockOpen] = useState(false);
  const [selectedSku, setSelectedSku] = useState(null);

  // Add Item Form
  const [addForm, setAddForm] = useState({
    sku: '',
    name: '',
    description: '',
    quantityOnHand: 100,
    reorderThreshold: 20,
    unitPriceUSD: 25.0,
    warehouseCode: 'WH-LAX-01'
  });

  // Restock Form
  const [restockQty, setRestockQty] = useState(50);

  const loadInventory = async () => {
    setLoading(true);
    try {
      if (showLowStockOnly) {
        const data = await api.inventory.getLowStock();
        setItems(data || []);
      } else {
        const data = await api.inventory.getAll();
        setItems(data || []);
      }
    } catch (err) {
      toastError('Failed to load inventory catalog: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadInventory();
  }, [showLowStockOnly]);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await api.inventory.create(addForm);
      toastSuccess(`Inventory item ${addForm.sku} created.`);
      setIsAddOpen(false);
      setAddForm({
        sku: '',
        name: '',
        description: '',
        quantityOnHand: 100,
        reorderThreshold: 20,
        unitPriceUSD: 25.0,
        warehouseCode: 'WH-LAX-01'
      });
      loadInventory();
    } catch (err) {
      toastError('Creation failed: ' + err.message);
    }
  };

  const handleRestock = async (e) => {
    e.preventDefault();
    if (!selectedSku || restockQty <= 0) return;
    try {
      const updated = await api.inventory.restock(selectedSku, restockQty);
      toastSuccess(`Restocked ${selectedSku}. New balance: ${updated.quantityOnHand || 'Updated'}`);
      setIsRestockOpen(false);
      loadInventory();
    } catch (err) {
      toastError('Restock failed: ' + err.message);
    }
  };

  const handleDelete = async (id, sku) => {
    if (!window.confirm(`Are you sure you want to remove item ${sku}?`)) return;
    try {
      await api.inventory.delete(id);
      toastSuccess(`Item ${sku} removed.`);
      loadInventory();
    } catch (err) {
      toastError('Deletion failed: ' + err.message);
    }
  };

  const filteredItems = items.filter((it) => {
    const term = searchTerm.toLowerCase();
    return (
      (it.sku && it.sku.toLowerCase().includes(term)) ||
      (it.name && it.name.toLowerCase().includes(term)) ||
      (it.warehouseCode && it.warehouseCode.toLowerCase().includes(term))
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-white tracking-tight">Warehouse & Stock Inventory</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            Real-time warehouse SKU catalog, replenishment thresholds, and automated restock triggers.
          </p>
        </div>

        {hasRole('ADMIN', 'LOGISTIC_PERSONNEL') && (
          <button
            onClick={() => setIsAddOpen(true)}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg shadow-blue-500/20 transition self-start sm:self-auto"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Inventory Item</span>
          </button>
        )}
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col md:flex-row items-center justify-between gap-3 bg-[#121929] p-3 rounded-2xl border border-gray-800">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-gray-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search inventory by SKU, name, or warehouse..."
            className="w-full bg-[#0d1424] border border-gray-700/80 rounded-xl py-2 pl-10 pr-4 text-xs text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
          />
        </div>

        <button
          onClick={() => setShowLowStockOnly(!showLowStockOnly)}
          className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold border transition ${
            showLowStockOnly
              ? 'bg-rose-500/20 border-rose-500/50 text-rose-300'
              : 'bg-gray-800/80 border-gray-700 text-gray-300 hover:bg-gray-700'
          }`}
        >
          <AlertTriangle className="w-3.5 h-3.5 text-rose-400" />
          <span>{showLowStockOnly ? 'Showing Low Stock Only' : 'Filter Low Stock'}</span>
        </button>
      </div>

      {/* Inventory Table */}
      <div className="bg-[#121929] border border-gray-800 rounded-3xl overflow-hidden shadow-xl">
        {loading ? (
          <div className="py-16 text-center text-xs text-gray-400">Loading catalog...</div>
        ) : filteredItems.length === 0 ? (
          <div className="py-16 text-center text-xs text-gray-400">
            No inventory records found.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#0d1424] border-b border-gray-800 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <tr>
                  <th className="py-3.5 px-4">SKU</th>
                  <th className="py-3.5 px-4">Item Name</th>
                  <th className="py-3.5 px-4">Warehouse</th>
                  <th className="py-3.5 px-4">Unit Price</th>
                  <th className="py-3.5 px-4">Stock Level</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {filteredItems.map((it) => {
                  const isLow = it.quantityOnHand <= (it.reorderThreshold || 20);
                  return (
                    <tr key={it.id || it.sku} className="hover:bg-gray-800/30 transition">
                      <td className="py-3 px-4 font-mono font-bold text-blue-400">
                        {it.sku}
                      </td>
                      <td className="py-3 px-4">
                        <div className="font-semibold text-white">{it.name}</div>
                        <div className="text-[10px] text-gray-400 truncate max-w-xs">{it.description}</div>
                      </td>
                      <td className="py-3 px-4 font-mono text-cyan-300">
                        {it.warehouseCode || 'WH-MAIN'}
                      </td>
                      <td className="py-3 px-4 font-mono font-semibold text-emerald-400">
                        ${it.unitPriceUSD?.toFixed(2)}
                      </td>
                      <td className="py-3 px-4 font-mono">
                        <div className="font-bold text-white">{it.quantityOnHand} units</div>
                        <div className="text-[10px] text-gray-400">Reorder at: {it.reorderThreshold}</div>
                      </td>
                      <td className="py-3 px-4">
                        <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                          isLow
                            ? 'bg-rose-500/20 text-rose-300 border-rose-500/40 animate-pulse'
                            : 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40'
                        }`}>
                          {isLow ? 'LOW STOCK' : 'IN STOCK'}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right space-x-1.5 font-sans">
                        {hasRole('ADMIN', 'LOGISTIC_PERSONNEL') && (
                          <>
                            <button
                              onClick={() => {
                                setSelectedSku(it.sku);
                                setRestockQty(50);
                                setIsRestockOpen(true);
                              }}
                              className="px-2.5 py-1 rounded-lg bg-blue-900/40 hover:bg-blue-800/60 text-blue-300 border border-blue-500/30 font-bold text-[11px] transition"
                            >
                              Restock
                            </button>
                            <button
                              onClick={() => handleDelete(it.id, it.sku)}
                              className="p-1 rounded-lg bg-gray-800 hover:bg-rose-950/40 text-gray-400 hover:text-rose-400 transition"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Add Item Modal */}
      <Modal
        isOpen={isAddOpen}
        onClose={() => setIsAddOpen(false)}
        title="Add Inventory Item"
        subtitle="Catalog a new SKU into warehouse inventory"
      >
        <form onSubmit={handleCreate} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">SKU</label>
              <input
                type="text"
                placeholder="SKU-MED-001"
                value={addForm.sku}
                onChange={(e) => setAddForm({ ...addForm, sku: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Warehouse Code</label>
              <input
                type="text"
                placeholder="WH-LAX-01"
                value={addForm.warehouseCode}
                onChange={(e) => setAddForm({ ...addForm, warehouseCode: e.target.value })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white font-mono"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Item Name</label>
            <input
              type="text"
              placeholder="e.g. Cold Chain Insulin Vials"
              value={addForm.name}
              onChange={(e) => setAddForm({ ...addForm, name: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Description</label>
            <textarea
              placeholder="Detailed item notes..."
              value={addForm.description}
              onChange={(e) => setAddForm({ ...addForm, description: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              rows={2}
            />
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Initial Qty</label>
              <input
                type="number"
                min="0"
                value={addForm.quantityOnHand}
                onChange={(e) => setAddForm({ ...addForm, quantityOnHand: parseInt(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Reorder Limit</label>
              <input
                type="number"
                min="0"
                value={addForm.reorderThreshold}
                onChange={(e) => setAddForm({ ...addForm, reorderThreshold: parseInt(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Unit Price ($)</label>
              <input
                type="number"
                step="0.01"
                value={addForm.unitPriceUSD}
                onChange={(e) => setAddForm({ ...addForm, unitPriceUSD: parseFloat(e.target.value) || 0 })}
                className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
                required
              />
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsAddOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs text-gray-300 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg"
            >
              Create Item
            </button>
          </div>
        </form>
      </Modal>

      {/* Restock Modal */}
      <Modal
        isOpen={isRestockOpen}
        onClose={() => setIsRestockOpen(false)}
        title={`Restock Item: ${selectedSku}`}
        subtitle="Increment warehouse inventory quantity on hand"
      >
        <form onSubmit={handleRestock} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">
              Quantity to Ingest (Units)
            </label>
            <input
              type="number"
              min="1"
              value={restockQty}
              onChange={(e) => setRestockQty(parseInt(e.target.value) || 1)}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white text-center font-bold text-base"
              required
            />
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t border-gray-800">
            <button
              type="button"
              onClick={() => setIsRestockOpen(false)}
              className="px-4 py-2 rounded-xl bg-gray-800 text-xs text-gray-300 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-bold text-white shadow-lg"
            >
              Confirm Restock
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
