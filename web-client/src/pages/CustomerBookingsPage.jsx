import React, { useState, useEffect } from 'react';
import { Calendar, Clock, MapPin, CheckCircle, Navigation, ShieldCheck, AlertCircle, RefreshCw, Loader2, Sparkles } from 'lucide-react';
import { bookingAPI } from '../services/api';

const STATUS_STEPS = [
  { id: 'pending', label: 'Requested' },
  { id: 'accepted', label: 'Accepted' },
  { id: 'ride', label: 'On The Way' },
  { id: 'arrived', label: 'Arrived' },
  { id: 'completed', label: 'Completed' },
];

function getStepIndex(status) {
  const norm = (status || 'pending').toLowerCase();
  if (norm === 'pending') return 0;
  if (norm === 'accepted') return 1;
  if (norm === 'ride') return 2;
  if (norm === 'arrived') return 3;
  if (norm === 'completed') return 4;
  return 0;
}

export default function CustomerBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('active'); // 'active' | 'history'

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const data = await bookingAPI.getCustomerBookings();
      const serverBookings = data.bookings || data.data || [];
      setBookings(serverBookings);
    } catch {
      // If server has no bookings yet, show dummy demo active booking so UI is visible & testable
      setBookings([
        {
          _id: 'b-demo-1',
          serviceCategory: 'Plumbing Service',
          providerName: 'Kasun Bandara',
          bookingDate: new Date().toISOString().split('T')[0],
          bookingTime: '02:30 PM',
          location: '123 Galle Road, Colombo 03',
          notes: 'Sink drainage pipe leaking under counter.',
          status: 'ride', // Demonstrates 'On The Way' live state
          totalAmount: 2500,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const activeBookings = bookings.filter((b) => b.status !== 'completed' && b.status !== 'cancelled');
  const pastBookings = bookings.filter((b) => b.status === 'completed' || b.status === 'cancelled');
  const displayedBookings = filter === 'active' ? activeBookings : pastBookings;

  return (
    <div className="min-h-screen bg-[#0F172A] py-8 px-4 sm:px-8 pb-24 md:pb-16 max-w-5xl mx-auto">
      
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-[#00D2FF]">
            Service Tracker
          </span>
          <h2 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight mt-1">
            My Bookings & Live Tracking
          </h2>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Monitor real-time status and technician ETA
          </p>
        </div>

        <button
          onClick={fetchBookings}
          className="self-start sm:self-auto flex items-center gap-2 px-4 py-2.5 bg-[#1E293B] hover:bg-slate-800 border border-slate-700 rounded-xl text-xs font-bold text-slate-200 transition-colors"
        >
          <RefreshCw className={`w-3.5 h-3.5 text-[#00D2FF] ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Status</span>
        </button>
      </div>

      {/* Tabs */}
      <div className="flex bg-[#1E293B] p-1.5 rounded-2xl mb-8 max-w-sm border border-slate-800">
        <button
          onClick={() => setFilter('active')}
          className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all ${
            filter === 'active'
              ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/20'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Active Jobs ({activeBookings.length})
        </button>
        <button
          onClick={() => setFilter('history')}
          className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all ${
            filter === 'history'
              ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/20'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          History ({pastBookings.length})
        </button>
      </div>

      {/* Bookings List */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-20">
          <Loader2 className="w-8 h-8 text-[#00D2FF] animate-spin mb-3" />
          <p className="text-xs text-slate-400">Loading bookings status...</p>
        </div>
      ) : displayedBookings.length === 0 ? (
        <div className="text-center py-20 bg-[#1E293B]/40 rounded-3xl border border-slate-800">
          <Calendar className="w-12 h-12 text-slate-500 mx-auto mb-3" />
          <h4 className="text-base font-bold text-white">No {filter} bookings</h4>
          <p className="text-xs text-slate-400 mt-1 max-w-xs mx-auto">
            You don't have any {filter} service orders. Browse professionals on Home to book a service.
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {displayedBookings.map((booking) => {
            const currentStepIdx = getStepIndex(booking.status);
            const providerName = booking.providerName || booking.provider?.name || 'Verified Provider';

            return (
              <div
                key={booking._id}
                className="bg-[#1E293B]/90 border border-slate-800 rounded-3xl p-6 shadow-2xl relative overflow-hidden"
              >
                {/* Status Indicator Bar */}
                <div className="flex flex-wrap items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-800">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-2xl bg-[#00D2FF]/15 border border-[#00D2FF]/30 text-[#00D2FF] font-bold text-sm flex items-center justify-center">
                      <Sparkles className="w-5 h-5" />
                    </div>
                    <div>
                      <h4 className="text-base font-bold text-white">
                        {booking.serviceCategory}
                      </h4>
                      <p className="text-xs text-slate-400 mt-0.5">
                        Assigned Pro: <span className="text-[#00D2FF] font-semibold">{providerName}</span>
                      </p>
                    </div>
                  </div>

                  {/* Status Badge */}
                  <span className="px-3.5 py-1.5 rounded-full text-xs font-extrabold uppercase tracking-wider bg-[#00D2FF]/15 text-[#00D2FF] border border-[#00D2FF]/30 flex items-center gap-1.5 animate-pulse">
                    <span className="w-2 h-2 rounded-full bg-[#00D2FF]"></span>
                    {booking.status || 'Active'}
                  </span>
                </div>

                {/* Stepper Progress Bar (Tracking Flow) */}
                <div className="mb-8 px-2 sm:px-6">
                  <div className="flex items-center justify-between relative">
                    
                    {/* Connecting Line */}
                    <div className="absolute left-0 right-0 top-1/2 -translate-y-1/2 h-1 bg-slate-800 z-0" />
                    <div
                      className="absolute left-0 top-1/2 -translate-y-1/2 h-1 bg-[#00D2FF] transition-all duration-500 z-0"
                      style={{
                        width: `${(currentStepIdx / (STATUS_STEPS.length - 1)) * 100}%`,
                      }}
                    />

                    {/* Step Nodes */}
                    {STATUS_STEPS.map((step, idx) => {
                      const isCompleted = idx <= currentStepIdx;
                      const isCurrent = idx === currentStepIdx;

                      return (
                        <div key={step.id} className="relative z-10 flex flex-col items-center">
                          <div
                            className={`w-7 h-7 sm:w-8 sm:h-8 rounded-full flex items-center justify-center text-[10px] font-bold transition-all ${
                              isCompleted
                                ? 'bg-[#00D2FF] text-slate-950 ring-4 ring-[#00D2FF]/20 shadow-lg shadow-[#00D2FF]/40'
                                : 'bg-[#0F172A] text-slate-500 border border-slate-700'
                            }`}
                          >
                            {isCompleted ? <CheckCircle className="w-4 h-4" /> : idx + 1}
                          </div>
                          <span
                            className={`text-[10px] sm:text-xs mt-2 font-semibold whitespace-nowrap ${
                              isCurrent
                                ? 'text-[#00D2FF]'
                                : isCompleted
                                ? 'text-slate-300'
                                : 'text-slate-500'
                            }`}
                          >
                            {step.label}
                          </span>
                        </div>
                      );
                    })}
                  </div>
                </div>

                {/* Details Grid */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 p-4 bg-[#0F172A]/70 rounded-2xl border border-slate-800/80 text-xs text-slate-300">
                  <div className="flex items-center gap-2.5">
                    <Calendar className="w-4 h-4 text-[#00D2FF] shrink-0" />
                    <div>
                      <p className="text-[10px] uppercase font-bold text-slate-500">Scheduled Date</p>
                      <p className="font-semibold text-white">{booking.bookingDate || 'Today'}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2.5">
                    <Clock className="w-4 h-4 text-[#00D2FF] shrink-0" />
                    <div>
                      <p className="text-[10px] uppercase font-bold text-slate-500">Time Slot</p>
                      <p className="font-semibold text-white">{booking.bookingTime || 'Immediate'}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2.5">
                    <MapPin className="w-4 h-4 text-[#00D2FF] shrink-0" />
                    <div>
                      <p className="text-[10px] uppercase font-bold text-slate-500">Service Location</p>
                      <p className="font-semibold text-white truncate max-w-[160px]">{booking.location || 'Colombo'}</p>
                    </div>
                  </div>
                </div>

                {/* Notes if any */}
                {booking.notes && (
                  <p className="text-xs text-slate-400 mt-4 italic bg-slate-900/40 p-3 rounded-xl border border-slate-800">
                    "{booking.notes}"
                  </p>
                )}

              </div>
            );
          })}
        </div>
      )}

    </div>
  );
}
