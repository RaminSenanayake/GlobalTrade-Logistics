import React, { useState } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import { Modal } from '../components/common/Modal';
import {
  Layers,
  FileText,
  Plus,
  Trash2,
  Play,
  Printer,
  CheckCircle2,
  AlertTriangle,
  Sparkles,
  ArrowRight
} from 'lucide-react';

export const BatchOperationsView = () => {
  const { user } = useAuth();
  const { error: toastError, success: toastSuccess } = useToast();

  const [activeTab, setActiveTab] = useState('dispatch'); // 'dispatch' or 'manifest'
  const [loading, setLoading] = useState(false);

  // Batch Dispatch Items List
  const [batchItems, setBatchItems] = useState([
    {
      origin: 'USA',
      destination: 'SGP',
      senderUsername: user?.username || 'customer1',
      carrier: 'Pacific Express Line',
      weightKg: 120.0,
      declaredValue: 4500.0,
      itemSku: 'SKU-MED-001',
      itemQty: 25
    },
    {
      origin: 'DEU',
      destination: 'JPN',
      senderUsername: user?.username || 'customer1',
      carrier: 'DHL Global Forwarding',
      weightKg: 350.0,
      declaredValue: 12500.0,
      itemSku: 'SKU-ELEC-002',
      itemQty: 60
    }
  ]);

  const [dispatchResult, setDispatchResult] = useState(null);

  // Manifest Generator State
  const [manifestTrackingInput, setManifestTrackingInput] = useState('TRK-US-2026-0001, TRK-DE-2026-0002');
  const [manifestOutput, setManifestOutput] = useState(null);

  const handleAddBatchRow = () => {
    setBatchItems([
      ...batchItems,
      {
        origin: 'USA',
        destination: 'CAN',
        senderUsername: user?.username || 'customer1',
        carrier: 'FedEx Express',
        weightKg: 50.0,
        declaredValue: 1800.0,
        itemSku: 'SKU-MED-001',
        itemQty: 10
      }
    ]);
  };

  const handleRemoveBatchRow = (index) => {
    setBatchItems(batchItems.filter((_, i) => i !== index));
  };

  const handleLoadSampleBatch = () => {
    setBatchItems([
      { origin: 'USA', destination: 'SGP', senderUsername: 'customer1', carrier: 'Pacific Cargo', weightKg: 100.0, declaredValue: 5000.0, itemSku: 'SKU-MED-001', itemQty: 20 },
      { origin: 'DEU', destination: 'USA', senderUsername: 'customer1', carrier: 'Atlantic Freight', weightKg: 240.0, declaredValue: 9800.0, itemSku: 'SKU-ELEC-002', itemQty: 40 },
      { origin: 'SGP', destination: 'AUS', senderUsername: 'customer1', carrier: 'TransGlobal Express', weightKg: 80.0, declaredValue: 2400.0, itemSku: 'SKU-AUTO-003', itemQty: 5 }
    ]);
    toastSuccess('Loaded 3 sample batch dispatch consignments.');
  };

  const handleProcessBatch = async () => {
    if (batchItems.length === 0) {
      toastError('Please add at least one dispatch item.');
      return;
    }

    setLoading(true);
    setDispatchResult(null);
    try {
      const result = await api.batch.dispatch(batchItems);
      setDispatchResult(result);
      toastSuccess(`Batch dispatch executed: ${result.successfulDispatches} created.`);
    } catch (err) {
      toastError('Batch dispatch failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateManifest = async (e) => {
    e?.preventDefault();
    const trackingList = manifestTrackingInput
      .split(/[,\n]+/)
      .map((t) => t.trim())
      .filter((t) => t.length > 0);

    if (trackingList.length === 0) {
      toastError('Please enter at least one tracking number.');
      return;
    }

    setLoading(true);
    try {
      const res = await api.batch.generateManifest(trackingList);
      setManifestOutput(res.manifest || 'No manifest output received');
      toastSuccess('Consolidated Cargo Manifest generated.');
    } catch (err) {
      toastError('Manifest generation error: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handlePrintManifest = () => {
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
      <html>
        <head>
          <title>Consolidated Cargo Manifest</title>
          <style>
            body { font-family: monospace; padding: 30px; white-space: pre-wrap; font-size: 13px; }
          </style>
        </head>
        <body>${manifestOutput}</body>
      </html>
    `);
    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-xl font-bold text-white tracking-tight">Batch Logistics & Cargo Manifest Generation</h1>
        <p className="text-xs text-gray-400 mt-0.5">
          High-throughput bulk dispatch processing and consolidated customs cargo manifest generation.
        </p>
      </div>

      {/* Navigation Tabs */}
      <div className="flex items-center gap-3 border-b border-gray-800 pb-2">
        <button
          onClick={() => setActiveTab('dispatch')}
          className={`flex items-center gap-2 text-xs font-bold pb-2 border-b-2 transition ${
            activeTab === 'dispatch'
              ? 'border-blue-500 text-blue-400'
              : 'border-transparent text-gray-400 hover:text-gray-200'
          }`}
        >
          <Layers className="w-4 h-4" />
          <span>Batch Dispatch Operations</span>
        </button>

        <button
          onClick={() => setActiveTab('manifest')}
          className={`flex items-center gap-2 text-xs font-bold pb-2 border-b-2 transition ${
            activeTab === 'manifest'
              ? 'border-cyan-500 text-cyan-400'
              : 'border-transparent text-gray-400 hover:text-gray-200'
          }`}
        >
          <FileText className="w-4 h-4" />
          <span>Consolidated Cargo Manifest</span>
        </button>
      </div>

      {/* Tab 1: Batch Dispatch Operations */}
      {activeTab === 'dispatch' && (
        <div className="space-y-6 animate-fade-in">
          <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div>
                <h2 className="text-base font-bold text-white">Bulk Consignment Dispatch Builder</h2>
                <p className="text-xs text-gray-400">Process hundreds of shipments in atomic JPA transaction batches.</p>
              </div>

              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={handleLoadSampleBatch}
                  className="px-3 py-1.5 rounded-xl bg-gray-800 hover:bg-gray-700 border border-gray-700 text-xs text-gray-200 font-semibold"
                >
                  Load Sample Batch
                </button>
                <button
                  type="button"
                  onClick={handleAddBatchRow}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-blue-600/20 hover:bg-blue-600/30 text-blue-300 border border-blue-500/30 text-xs font-bold"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>Add Row</span>
                </button>
              </div>
            </div>

            {/* Batch Table */}
            <div className="overflow-x-auto rounded-2xl border border-gray-800">
              <table className="w-full text-left text-xs">
                <thead className="bg-[#0d1424] text-[10px] uppercase font-bold text-gray-400">
                  <tr>
                    <th className="p-3">Route (From &rarr; To)</th>
                    <th className="p-3">Carrier</th>
                    <th className="p-3">SKU</th>
                    <th className="p-3">Qty</th>
                    <th className="p-3">Weight (kg)</th>
                    <th className="p-3">Declared ($)</th>
                    <th className="p-3 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800 font-mono text-xs">
                  {batchItems.map((item, idx) => (
                    <tr key={idx} className="hover:bg-gray-800/30">
                      <td className="p-2.5">
                        <div className="flex items-center gap-1">
                          <input
                            type="text"
                            value={item.origin}
                            onChange={(e) => {
                              const copy = [...batchItems];
                              copy[idx].origin = e.target.value;
                              setBatchItems(copy);
                            }}
                            className="w-14 bg-[#0d1424] border border-gray-700 rounded p-1 text-white uppercase text-center"
                          />
                          <span className="text-gray-500">&rarr;</span>
                          <input
                            type="text"
                            value={item.destination}
                            onChange={(e) => {
                              const copy = [...batchItems];
                              copy[idx].destination = e.target.value;
                              setBatchItems(copy);
                            }}
                            className="w-14 bg-[#0d1424] border border-gray-700 rounded p-1 text-white uppercase text-center"
                          />
                        </div>
                      </td>
                      <td className="p-2.5">
                        <input
                          type="text"
                          value={item.carrier}
                          onChange={(e) => {
                            const copy = [...batchItems];
                            copy[idx].carrier = e.target.value;
                            setBatchItems(copy);
                          }}
                          className="w-32 bg-[#0d1424] border border-gray-700 rounded p-1 text-white font-sans"
                        />
                      </td>
                      <td className="p-2.5">
                        <input
                          type="text"
                          value={item.itemSku}
                          onChange={(e) => {
                            const copy = [...batchItems];
                            copy[idx].itemSku = e.target.value;
                            setBatchItems(copy);
                          }}
                          className="w-28 bg-[#0d1424] border border-gray-700 rounded p-1 text-blue-400 font-bold"
                        />
                      </td>
                      <td className="p-2.5">
                        <input
                          type="number"
                          value={item.itemQty}
                          onChange={(e) => {
                            const copy = [...batchItems];
                            copy[idx].itemQty = parseInt(e.target.value) || 1;
                            setBatchItems(copy);
                          }}
                          className="w-16 bg-[#0d1424] border border-gray-700 rounded p-1 text-white text-right"
                        />
                      </td>
                      <td className="p-2.5">
                        <input
                          type="number"
                          step="0.1"
                          value={item.weightKg}
                          onChange={(e) => {
                            const copy = [...batchItems];
                            copy[idx].weightKg = parseFloat(e.target.value) || 0;
                            setBatchItems(copy);
                          }}
                          className="w-20 bg-[#0d1424] border border-gray-700 rounded p-1 text-white text-right"
                        />
                      </td>
                      <td className="p-2.5">
                        <input
                          type="number"
                          step="0.01"
                          value={item.declaredValue}
                          onChange={(e) => {
                            const copy = [...batchItems];
                            copy[idx].declaredValue = parseFloat(e.target.value) || 0;
                            setBatchItems(copy);
                          }}
                          className="w-24 bg-[#0d1424] border border-gray-700 rounded p-1 text-emerald-400 font-bold text-right"
                        />
                      </td>
                      <td className="p-2.5 text-right">
                        <button
                          type="button"
                          onClick={() => handleRemoveBatchRow(idx)}
                          className="p-1 text-gray-400 hover:text-rose-400 transition"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="flex justify-end pt-2">
              <button
                onClick={handleProcessBatch}
                disabled={loading}
                className="flex items-center gap-2 px-6 py-3 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-xs font-bold text-white shadow-lg shadow-blue-500/25 transition disabled:opacity-50"
              >
                <Play className="w-4 h-4" />
                <span>{loading ? 'Executing Batch...' : `Execute Dispatch Batch (${batchItems.length} items)`}</span>
              </button>
            </div>
          </div>

          {/* Results Card */}
          {dispatchResult && (
            <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4 animate-fade-in">
              <div className="flex items-center justify-between pb-3 border-b border-gray-800">
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="w-5 h-5 text-emerald-400" />
                  <h3 className="text-sm font-bold text-white">Batch Execution Telemetry Report</h3>
                </div>
                <div className="flex gap-4 text-xs font-mono">
                  <span className="text-emerald-400">Success: <strong>{dispatchResult.successfulDispatches}</strong></span>
                  <span className="text-rose-400">Failed: <strong>{dispatchResult.failedDispatches}</strong></span>
                </div>
              </div>

              {dispatchResult.trackingNumbers && dispatchResult.trackingNumbers.length > 0 && (
                <div>
                  <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">
                    Generated Consignment Tracking Numbers
                  </h4>
                  <div className="flex flex-wrap gap-2">
                    {dispatchResult.trackingNumbers.map((trk, i) => (
                      <span key={i} className="px-3 py-1 rounded-lg bg-blue-950/60 border border-blue-500/30 text-blue-300 font-mono text-xs font-bold">
                        {trk}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {dispatchResult.errors && dispatchResult.errors.length > 0 && (
                <div>
                  <h4 className="text-xs font-bold text-rose-400 uppercase tracking-wider mb-2">
                    Batch Dispatch Failure Log
                  </h4>
                  <div className="bg-rose-950/30 border border-rose-500/30 p-3 rounded-xl space-y-1 text-xs text-rose-200 font-mono">
                    {dispatchResult.errors.map((err, i) => (
                      <div key={i}>&bull; {err}</div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Manifest Generator */}
      {activeTab === 'manifest' && (
        <div className="space-y-6 animate-fade-in">
          <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4">
            <h2 className="text-base font-bold text-white">Consolidated Cargo Manifest Generator</h2>
            <p className="text-xs text-gray-400">Enter comma-separated tracking numbers to compile a certified customs manifest document.</p>

            <form onSubmit={handleGenerateManifest} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">
                  Consignment Tracking Numbers (comma or newline separated)
                </label>
                <textarea
                  value={manifestTrackingInput}
                  onChange={(e) => setManifestTrackingInput(e.target.value)}
                  rows={3}
                  className="w-full bg-[#0d1424] border border-gray-700 rounded-xl p-3 text-xs text-white font-mono focus:outline-none focus:border-cyan-500"
                  required
                />
              </div>

              <div className="flex justify-end">
                <button
                  type="submit"
                  disabled={loading}
                  className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-xs font-bold text-white shadow-lg shadow-cyan-500/20"
                >
                  <Sparkles className="w-4 h-4" />
                  <span>Compile Official Manifest</span>
                </button>
              </div>
            </form>
          </div>

          {manifestOutput && (
            <div className="bg-[#121929] border border-gray-800 rounded-3xl p-6 shadow-xl space-y-4 animate-fade-in">
              <div className="flex items-center justify-between pb-3 border-b border-gray-800">
                <h3 className="text-sm font-bold text-white flex items-center gap-2">
                  <FileText className="w-4 h-4 text-cyan-400" />
                  <span>Certified Customs Cargo Manifest</span>
                </h3>
                <button
                  onClick={handlePrintManifest}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-xs font-bold text-gray-200 border border-gray-700"
                >
                  <Printer className="w-3.5 h-3.5" />
                  <span>Print Document</span>
                </button>
              </div>

              <div className="bg-white text-gray-900 p-6 rounded-2xl font-mono text-xs shadow-inner whitespace-pre-wrap overflow-x-auto max-h-[500px] border border-gray-300 custom-scrollbar">
                {manifestOutput}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
