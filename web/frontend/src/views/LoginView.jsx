import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/common/Toast';
import { Ship, Lock, User, ArrowRight, ShieldCheck, Zap } from 'lucide-react';

export const LoginView = () => {
  const { login } = useAuth();
  const { error: toastError, success: toastSuccess } = useToast();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e?.preventDefault();
    if (!username || !password) {
      toastError('Please enter username and password');
      return;
    }

    setLoading(true);
    try {
      await login(username, password);
      toastSuccess(`Welcome back, ${username}!`);
    } catch (err) {
      toastError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickFill = (user, pass) => {
    setUsername(user);
    setPassword(pass);
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center p-4 bg-[#080d1a] relative overflow-hidden">
      {/* Dynamic Background Glows */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md bg-[#131b2e] border border-gray-700/60 rounded-3xl p-8 shadow-2xl relative z-10">
        {/* Brand Logo & Title */}
        <div className="text-center mb-8">
          <div className="w-14 h-14 mx-auto rounded-2xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-400 flex items-center justify-center text-white shadow-xl shadow-blue-500/25 mb-4">
            <Ship className="w-7 h-7" />
          </div>
          <h1 className="text-2xl font-black text-white tracking-tight">GlobalTrade Logistics</h1>
          <p className="text-xs text-cyan-400 font-bold uppercase tracking-widest mt-1">Enterprise Supply Chain Platform</p>
        </div>

        {/* Login Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Username
            </label>
            <div className="relative">
              <User className="w-4 h-4 text-gray-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="e.g. admin"
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl py-2.5 pl-10 pr-4 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Password
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-gray-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
                className="w-full bg-[#0d1424] border border-gray-700 rounded-xl py-2.5 pl-10 pr-4 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white font-bold py-3 px-4 rounded-xl shadow-lg shadow-blue-500/25 transition duration-200 flex items-center justify-center gap-2 group disabled:opacity-50"
          >
            {loading ? (
              <span className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            ) : (
              <>
                <span>Sign In to Platform</span>
                <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
              </>
            )}
          </button>
        </form>

        {/* Demo Roles Quick-Fill Panel */}
        <div className="mt-8 pt-6 border-t border-gray-800">
          <div className="flex items-center gap-1.5 text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">
            <Zap className="w-3.5 h-3.5 text-amber-400" />
            <span>Preset Demo Accounts</span>
          </div>

          <div className="grid grid-cols-2 gap-2 text-xs">
            <button
              type="button"
              onClick={() => handleQuickFill('admin', 'Admin@123')}
              className="p-2 bg-gray-800/60 hover:bg-gray-800 border border-gray-700/80 rounded-lg text-left transition"
            >
              <div className="font-bold text-purple-300">Admin</div>
              <div className="text-[10px] text-gray-400">admin / Admin@123</div>
            </button>

            <button
              type="button"
              onClick={() => handleQuickFill('logistics_mgr', 'Logistics@123')}
              className="p-2 bg-gray-800/60 hover:bg-gray-800 border border-gray-700/80 rounded-lg text-left transition"
            >
              <div className="font-bold text-blue-300">Logistics Mgr</div>
              <div className="text-[10px] text-gray-400">logistics_mgr / Logistics@123</div>
            </button>

            <button
              type="button"
              onClick={() => handleQuickFill('customs_agent', 'Customs@123')}
              className="p-2 bg-gray-800/60 hover:bg-gray-800 border border-gray-700/80 rounded-lg text-left transition"
            >
              <div className="font-bold text-amber-300">Customs Agent</div>
              <div className="text-[10px] text-gray-400">customs_agent / Customs@123</div>
            </button>

            <button
              type="button"
              onClick={() => handleQuickFill('vendor_rep', 'Vendor@123')}
              className="p-2 bg-gray-800/60 hover:bg-gray-800 border border-gray-700/80 rounded-lg text-left transition"
            >
              <div className="font-bold text-emerald-300">Vendor Rep</div>
              <div className="text-[10px] text-gray-400">vendor_rep / Vendor@123</div>
            </button>

            <button
              type="button"
              onClick={() => handleQuickFill('customer1', 'Customer@123')}
              className="col-span-2 p-2 bg-gray-800/60 hover:bg-gray-800 border border-gray-700/80 rounded-lg text-left transition"
            >
              <div className="font-bold text-cyan-300">Customer</div>
              <div className="text-[10px] text-gray-400">customer1 / Customer@123</div>
            </button>
          </div>
        </div>

        <div className="mt-6 text-center text-[11px] text-gray-400">
          Jakarta EE 10 / GlassFish 7 &bull; EJB 3.1+ Multi-Module EAR
        </div>
      </div>
    </div>
  );
};
