import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useToast } from '../components/common/Toast';
import { Modal } from '../components/common/Modal';
import {
  Users,
  UserPlus,
  ShieldCheck,
  Lock,
  Search,
  KeyRound
} from 'lucide-react';

export const UserManagementView = () => {
  const { error: toastError, success: toastSuccess } = useToast();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);

  const [form, setForm] = useState({
    username: '',
    password: '',
    role: 'LOGISTIC_PERSONNEL'
  });

  const loadUsers = async () => {
    setLoading(true);
    try {
      const data = await api.auth.getUsers();
      setUsers(data || []);
    } catch (err) {
      toastError('Failed to load users: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleRegisterUser = async (e) => {
    e.preventDefault();
    try {
      await api.auth.register(form.username, form.password, form.role);
      toastSuccess(`User ${form.username} registered with role ${form.role}.`);
      setIsRegisterOpen(false);
      setForm({ username: '', password: '', role: 'LOGISTIC_PERSONNEL' });
      loadUsers();
    } catch (err) {
      toastError('User registration failed: ' + err.message);
    }
  };

  const getRoleBadge = (role) => {
    switch (role) {
      case 'ADMIN': return 'bg-purple-500/20 text-purple-300 border-purple-500/40';
      case 'LOGISTIC_PERSONNEL': return 'bg-blue-500/20 text-blue-300 border-blue-500/40';
      case 'CUSTOM_OFFICIAL': return 'bg-amber-500/20 text-amber-300 border-amber-500/40';
      case 'VENDOR': return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40';
      case 'CUSTOMER': return 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40';
      default: return 'bg-gray-500/20 text-gray-300 border-gray-500/40';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-white tracking-tight">Enterprise User Access Control</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            Role-Based Access Control (RBAC) managed via Jakarta EE Security (<code>@DeclareRoles</code> &amp; <code>@RolesAllowed</code>).
          </p>
        </div>

        <button
          onClick={() => setIsRegisterOpen(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-xs font-bold text-white shadow-lg shadow-purple-500/20 transition self-start sm:self-auto"
        >
          <UserPlus className="w-3.5 h-3.5" />
          <span>Register New Principal</span>
        </button>
      </div>

      {/* Users Table */}
      <div className="bg-[#121929] border border-gray-800 rounded-3xl overflow-hidden shadow-xl">
        {loading ? (
          <div className="py-16 text-center text-xs text-gray-400">Loading user registry...</div>
        ) : users.length === 0 ? (
          <div className="py-16 text-center text-xs text-gray-400">No users found.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#0d1424] border-b border-gray-800 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <tr>
                  <th className="py-3.5 px-4">User Principal</th>
                  <th className="py-3.5 px-4">Granted Security Role</th>
                  <th className="py-3.5 px-4">PBKDF2 Password Hash</th>
                  <th className="py-3.5 px-4">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {users.map((u) => (
                  <tr key={u.id || u.username} className="hover:bg-gray-800/30 transition">
                    <td className="py-3 px-4 font-bold text-white flex items-center gap-2.5">
                      <div className="w-7 h-7 rounded-full bg-gray-800 border border-gray-700 flex items-center justify-center text-[11px] text-blue-400 font-mono">
                        {u.username.charAt(0).toUpperCase()}
                      </div>
                      <span>{u.username}</span>
                    </td>
                    <td className="py-3 px-4">
                      <span className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full border ${getRoleBadge(u.role)}`}>
                        {u.role}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-mono text-gray-400 text-[11px] truncate max-w-xs">
                      {u.passwordHash ? `${u.passwordHash.substring(0, 32)}...` : '••••••••••••••••'}
                    </td>
                    <td className="py-3 px-4">
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                        ACTIVE
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Register User Modal */}
      <Modal
        isOpen={isRegisterOpen}
        onClose={() => setIsRegisterOpen(false)}
        title="Provision New Enterprise User"
        subtitle="Registers an authenticated principal in PBKDF2 credential database"
      >
        <form onSubmit={handleRegisterUser} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Username</label>
            <input
              type="text"
              placeholder="e.g. customs_lead"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Password</label>
            <input
              type="password"
              placeholder="••••••••••••"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase mb-1">Assigned Security Role</label>
            <select
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
              className="w-full bg-[#111827] border border-gray-700 rounded-xl p-2.5 text-xs text-white focus:outline-none focus:border-purple-500"
            >
              <option value="LOGISTIC_PERSONNEL">LOGISTIC_PERSONNEL</option>
              <option value="CUSTOM_OFFICIAL">CUSTOM_OFFICIAL</option>
              <option value="VENDOR">VENDOR</option>
              <option value="CUSTOMER">CUSTOMER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
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
              className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-xs font-bold text-white shadow-lg"
            >
              Provision Account
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
