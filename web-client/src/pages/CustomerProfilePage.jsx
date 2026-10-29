import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, CreditCard, Bell, HelpCircle, ChevronRight, LogOut, Check, Shield } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function CustomerProfilePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [copied, setCopied] = useState(false);

  const name = user?.name || localStorage.getItem('customer_name') || 'Customer';
  const email = user?.email || 'customer@localpro.com';
  const initial = name.charAt(0).toUpperCase();
  const role = user?.role || localStorage.getItem('user_role') || 'customer';

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleCopyId = () => {
    navigator.clipboard?.writeText(user?._id || 'user-id');
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  return (
    <div className="min-h-screen bg-[#0F172A] py-8 px-4 sm:px-8 pb-28 md:pb-16 max-w-2xl mx-auto">
      
      {/* Profile Header Card */}
      <div className="bg-[#1E293B] border border-slate-800 rounded-3xl p-8 flex flex-col items-center text-center shadow-2xl relative overflow-hidden mb-8">
        
        {/* Glow Accent */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-48 h-20 bg-[#00D2FF]/15 blur-2xl rounded-full pointer-events-none" />

        {/* Big Avatar Circle */}
        <div className="relative mb-4">
          <div className="w-24 h-24 rounded-full bg-[#0F172A] border-4 border-[#00D2FF]/40 shadow-xl shadow-[#00D2FF]/20 flex items-center justify-center">
            <span className="text-4xl font-extrabold text-[#00D2FF]">
              {initial}
            </span>
          </div>
          <span className="absolute bottom-1 right-1 w-6 h-6 bg-emerald-500 border-3 border-[#1E293B] rounded-full flex items-center justify-center" title="Verified">
            <Shield className="w-3.5 h-3.5 text-white" />
          </span>
        </div>

        {/* User Name & Details */}
        <h2 className="text-2xl font-bold text-white tracking-tight">
          {name}
        </h2>
        <p className="text-sm text-slate-400 mt-0.5">
          {email}
        </p>

        {/* Role Pill */}
        <div className="mt-3 inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-[#00D2FF]/10 text-[#00D2FF] border border-[#00D2FF]/30">
          <span>{role === 'provider' ? 'Service Provider' : 'Customer Account'}</span>
        </div>
      </div>

      {/* Account Settings Section */}
      <div className="mb-6">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-4 mb-3">
          Account Settings
        </h3>

        <div className="bg-[#1E293B] border border-slate-800 rounded-3xl overflow-hidden shadow-xl">
          {/* Personal Information */}
          <button
            onClick={() => alert(`Profile Info:\nName: ${name}\nEmail: ${email}\nRole: ${role}`)}
            className="w-full flex items-center justify-between p-4 px-6 hover:bg-slate-800/60 transition-colors text-left"
          >
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-xl bg-[#0F172A] border border-slate-700/80 flex items-center justify-center text-[#00D2FF]">
                <User className="w-5 h-5" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Personal Information</p>
                <p className="text-xs text-slate-400">View and manage your account details</p>
              </div>
            </div>
            <ChevronRight className="w-5 h-5 text-slate-500" />
          </button>

          <div className="h-px bg-slate-800 mx-6" />

          {/* Payment Methods */}
          <button
            onClick={() => alert('Payment Gateways: Cash on Delivery / Direct Bank Transfer supported.')}
            className="w-full flex items-center justify-between p-4 px-6 hover:bg-slate-800/60 transition-colors text-left"
          >
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-xl bg-[#0F172A] border border-slate-700/80 flex items-center justify-center text-[#00D2FF]">
                <CreditCard className="w-5 h-5" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Payment Methods</p>
                <p className="text-xs text-slate-400">Cash on delivery, cards, and invoices</p>
              </div>
            </div>
            <ChevronRight className="w-5 h-5 text-slate-500" />
          </button>
        </div>
      </div>

      {/* App Settings Section */}
      <div className="mb-8">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-4 mb-3">
          App Settings
        </h3>

        <div className="bg-[#1E293B] border border-slate-800 rounded-3xl overflow-hidden shadow-xl">
          {/* Notifications */}
          <button
            onClick={() => alert('Push notifications via Firebase Cloud Messaging are enabled.')}
            className="w-full flex items-center justify-between p-4 px-6 hover:bg-slate-800/60 transition-colors text-left"
          >
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-xl bg-[#0F172A] border border-slate-700/80 flex items-center justify-center text-[#00D2FF]">
                <Bell className="w-5 h-5" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Notifications</p>
                <p className="text-xs text-slate-400">Booking alerts and status updates</p>
              </div>
            </div>
            <ChevronRight className="w-5 h-5 text-slate-500" />
          </button>

          <div className="h-px bg-slate-800 mx-6" />

          {/* Help Center */}
          <button
            onClick={() => alert('LocalPro Support:\nEmail: nimsarakavindhu@gmail.com\nHotline: +94 11 234 5678')}
            className="w-full flex items-center justify-between p-4 px-6 hover:bg-slate-800/60 transition-colors text-left"
          >
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-xl bg-[#0F172A] border border-slate-700/80 flex items-center justify-center text-[#00D2FF]">
                <HelpCircle className="w-5 h-5" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Help Center</p>
                <p className="text-xs text-slate-400">Customer care and FAQs</p>
              </div>
            </div>
            <ChevronRight className="w-5 h-5 text-slate-500" />
          </button>
        </div>
      </div>

      {/* Logout Button (Red Outline, exact match to bg_logout in Android) */}
      <button
        onClick={handleLogout}
        className="w-full py-4 rounded-2xl bg-[#2D1B2E] border border-rose-500/80 hover:bg-rose-500/20 text-rose-400 font-extrabold text-sm tracking-wider uppercase transition-all flex items-center justify-center gap-2 shadow-xl shadow-rose-950/30"
      >
        <LogOut className="w-4 h-4" />
        <span>Log Out</span>
      </button>

    </div>
  );
}
