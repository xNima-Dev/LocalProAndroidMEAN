import React, { useState } from 'react';
import { X, Calendar, Clock, MapPin, FileText, CheckCircle2, AlertCircle, Loader2 } from 'lucide-react';
import { bookingAPI } from '../services/api';

export default function BookingModal({ provider, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    bookingDate: new Date().toISOString().split('T')[0],
    bookingTime: '10:00 AM',
    address: '123 Galle Road, Colombo 03',
    notes: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleBookingSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await bookingAPI.createBooking({
        providerId: provider._id || provider.id,
        serviceCategory: provider.serviceCategory || provider.category || 'General',
        bookingDate: formData.bookingDate,
        bookingTime: formData.bookingTime,
        location: formData.address,
        notes: formData.notes,
        totalAmount: provider.hourlyRate || 2500,
      });

      setSuccess(true);
      setTimeout(() => {
        if (onSuccess) onSuccess();
      }, 1500);
    } catch (err) {
      setError(err.message || 'Failed to submit booking request.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-lg bg-[#1E293B] border border-slate-700/80 rounded-3xl shadow-2xl overflow-hidden relative">
        
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-slate-800">
          <div>
            <h3 className="text-lg font-bold text-white">Book Professional</h3>
            <p className="text-xs text-slate-400 mt-0.5">
              Service Request for <span className="text-[#00D2FF] font-semibold">{provider.name}</span>
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-full text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6">
          {success ? (
            <div className="text-center py-8">
              <CheckCircle2 className="w-16 h-16 text-emerald-400 mx-auto mb-4 animate-bounce" />
              <h4 className="text-xl font-bold text-white">Booking Request Sent!</h4>
              <p className="text-sm text-slate-300 mt-2 max-w-xs mx-auto">
                Your request has been forwarded to {provider.name}. Track the status live in My Bookings.
              </p>
            </div>
          ) : (
            <form onSubmit={handleBookingSubmit} className="space-y-4">
              {error && (
                <div className="flex items-start gap-3 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs">
                  <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
                  <span>{error}</span>
                </div>
              )}

              {/* Provider Quick Info Badge */}
              <div className="flex items-center justify-between p-3.5 bg-[#0F172A] rounded-2xl border border-slate-800">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-[#00D2FF]/20 text-[#00D2FF] font-bold text-sm flex items-center justify-center border border-[#00D2FF]/30">
                    {(provider.name ? provider.name.charAt(0) : 'P').toUpperCase()}
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-white">{provider.name}</h4>
                    <p className="text-xs text-[#00D2FF]">{provider.serviceCategory || provider.category || 'Service'}</p>
                  </div>
                </div>
                <div className="text-right">
                  <span className="text-xs text-slate-400">Rate</span>
                  <p className="text-sm font-bold text-emerald-400">
                    LKR {provider.hourlyRate || '2,500'}/hr
                  </p>
                </div>
              </div>

              {/* Date & Time Row */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-400 mb-1.5">
                    Date
                  </label>
                  <div className="relative">
                    <Calendar className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type="date"
                      required
                      name="bookingDate"
                      value={formData.bookingDate}
                      onChange={handleChange}
                      className="w-full bg-[#0F172A] border border-slate-700/80 rounded-xl pl-9 pr-3 py-2.5 text-xs text-white focus:outline-none focus:border-[#00D2FF]"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-400 mb-1.5">
                    Time Slot
                  </label>
                  <div className="relative">
                    <Clock className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type="text"
                      required
                      name="bookingTime"
                      value={formData.bookingTime}
                      onChange={handleChange}
                      placeholder="e.g. 10:00 AM"
                      className="w-full bg-[#0F172A] border border-slate-700/80 rounded-xl pl-9 pr-3 py-2.5 text-xs text-white focus:outline-none focus:border-[#00D2FF]"
                    />
                  </div>
                </div>
              </div>

              {/* Service Address */}
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-400 mb-1.5">
                  Service Address / Location
                </label>
                <div className="relative">
                  <MapPin className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
                  <input
                    type="text"
                    required
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    placeholder="Enter your home/work address"
                    className="w-full bg-[#0F172A] border border-slate-700/80 rounded-xl pl-9 pr-3 py-2.5 text-xs text-white focus:outline-none focus:border-[#00D2FF]"
                  />
                </div>
              </div>

              {/* Problem Details */}
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-400 mb-1.5">
                  Job Description / Details
                </label>
                <div className="relative">
                  <FileText className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
                  <textarea
                    rows="3"
                    name="notes"
                    value={formData.notes}
                    onChange={handleChange}
                    placeholder="Describe what needs fixing or specific instructions..."
                    className="w-full bg-[#0F172A] border border-slate-700/80 rounded-xl pl-9 pr-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-[#00D2FF] resize-none"
                  />
                </div>
              </div>

              {/* Confirm Submit Button */}
              <button
                type="submit"
                disabled={loading}
                className="w-full py-3 px-4 bg-[#00D2FF] hover:bg-[#00B4D8] text-slate-950 font-bold rounded-xl shadow-lg shadow-[#00D2FF]/20 flex items-center justify-center gap-2 text-sm transition-all disabled:opacity-50 mt-2"
              >
                {loading ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span>Confirming Request...</span>
                  </>
                ) : (
                  <span>Send Booking Request</span>
                )}
              </button>
            </form>
          )}
        </div>

      </div>
    </div>
  );
}
