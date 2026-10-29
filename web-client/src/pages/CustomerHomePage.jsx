import React, { useState, useEffect } from 'react';
import { Search, MapPin, Star, Wrench, Zap, Sparkles, Paintbrush, Hammer, Snowflake, Tv, ShieldCheck, ChevronRight, Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import BookingModal from '../components/BookingModal';

const CATEGORIES = [
  { id: 'All', name: 'All Services', icon: Sparkles },
  { id: 'Plumber', name: 'Plumber', icon: Wrench },
  { id: 'Electrician', name: 'Electrician', icon: Zap },
  { id: 'Cleaner', name: 'Cleaning', icon: Sparkles },
  { id: 'Painter', name: 'Painting', icon: Paintbrush },
  { id: 'Carpenter', name: 'Carpentry', icon: Hammer },
  { id: 'AC Repair', name: 'AC Repair', icon: Snowflake },
  { id: 'Appliance Repair', name: 'Appliances', icon: Tv },
];

const DEMO_PROVIDERS = [
  {
    _id: 'p1',
    name: 'Kasun Bandara',
    serviceCategory: 'Plumber',
    experience: '6 Years Experience',
    hourlyRate: 2500,
    rating: 4.9,
    distance: '1.2 km away',
    bio: 'Certified master plumber specializing in leak detection, bathroom fixtures, and emergency pipe repairs.',
  },
  {
    _id: 'p2',
    name: 'Nuwan Perera',
    serviceCategory: 'Electrician',
    experience: '8 Years Experience',
    hourlyRate: 3000,
    rating: 4.8,
    distance: '2.5 km away',
    bio: 'Residential and commercial wiring, short circuit troubleshooting, and high-load appliance installation.',
  },
  {
    _id: 'p3',
    name: 'Dilshan Silva',
    serviceCategory: 'AC Repair',
    experience: '5 Years Experience',
    hourlyRate: 3500,
    rating: 4.9,
    distance: '3.1 km away',
    bio: 'Split unit AC servicing, gas refilling, compressor diagnostics, and inverter repairs.',
  },
  {
    _id: 'p4',
    name: 'Supun Fernando',
    serviceCategory: 'Cleaner',
    experience: '4 Years Experience',
    hourlyRate: 2000,
    rating: 4.7,
    distance: '1.8 km away',
    bio: 'Deep home cleaning, sofa and carpet shampooing, post-construction cleanup with professional equipment.',
  },
];

export default function CustomerHomePage() {
  const { user } = useAuth();
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedProviderForBooking, setSelectedProviderForBooking] = useState(null);

  const customerName = user?.name || localStorage.getItem('customer_name') || 'Customer';
  const initial = customerName.charAt(0).toUpperCase();

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  };

  useEffect(() => {
    fetchProviders();
  }, [selectedCategory]);

  const fetchProviders = async () => {
    setLoading(true);
    try {
      const catParam = selectedCategory === 'All' ? '' : selectedCategory;
      const data = await authAPI.getNearProviders(catParam);
      
      const serverProviders = data.providers || data.data || [];
      if (serverProviders.length > 0) {
        setProviders(serverProviders);
      } else {
        if (selectedCategory === 'All') {
          setProviders(DEMO_PROVIDERS);
        } else {
          setProviders(DEMO_PROVIDERS.filter((p) => p.serviceCategory === selectedCategory));
        }
      }
    } catch {
      if (selectedCategory === 'All') {
        setProviders(DEMO_PROVIDERS);
      } else {
        setProviders(DEMO_PROVIDERS.filter((p) => p.serviceCategory === selectedCategory));
      }
    } finally {
      setLoading(false);
    }
  };

  const filteredProviders = providers.filter((p) => {
    const pName = p.name || '';
    const pCat = p.serviceCategory || p.providerProfile?.serviceCategory || '';
    const pBio = p.bio || p.providerProfile?.bio || '';

    return (
      pName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      pCat.toLowerCase().includes(searchQuery.toLowerCase()) ||
      pBio.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  return (
    <div className="min-h-screen bg-[#0F172A] pb-24 md:pb-16">
      
      {/* Top Greeting Header (Flush with top bar) */}
      <div className="bg-gradient-to-b from-[#1E293B] to-[#0F172A] border-b border-slate-800/80 px-4 sm:px-8 pt-8 pb-10">
        <div className="max-w-6xl mx-auto">
          
          <div className="flex items-center justify-between gap-4">
            <div>
              <p className="text-xs font-bold uppercase tracking-wider text-[#00D2FF]">
                {getGreeting()} 👋
              </p>
              <h2 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight mt-0.5">
                {customerName}
              </h2>
              <p className="text-xs sm:text-sm text-slate-400 mt-1 flex items-center gap-1.5">
                <MapPin className="w-3.5 h-3.5 text-[#00D2FF]" />
                Colombo, Western Province
              </p>
            </div>

            {/* Avatar Pill */}
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-[#00D2FF] to-cyan-300 p-0.5 shadow-lg shadow-[#00D2FF]/20">
              <div className="w-full h-full bg-[#0F172A] rounded-[14px] flex items-center justify-center">
                <span className="text-2xl font-black text-[#00D2FF]">{initial}</span>
              </div>
            </div>
          </div>

          {/* Search Bar */}
          <div className="mt-8 relative">
            <Search className="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search services, plumbers, electricians, AC repair..."
              className="w-full bg-[#1E293B] border border-slate-700/80 rounded-2xl pl-12 pr-4 py-4 text-sm text-white placeholder-slate-400 focus:outline-none focus:border-[#00D2FF] focus:ring-2 focus:ring-[#00D2FF]/20 shadow-xl transition-all"
            />
          </div>

        </div>
      </div>

      <div className="max-w-6xl mx-auto px-4 sm:px-8 mt-8">
        
        {/* Categories Section */}
        <div className="mb-10">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-[#00D2FF]" />
              Popular Services
            </h3>
            <span className="text-xs font-semibold text-[#00D2FF]">
              {CATEGORIES.length - 1} Categories
            </span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-3">
            {CATEGORIES.map((cat) => {
              const Icon = cat.icon;
              const isSelected = selectedCategory === cat.id;

              return (
                <button
                  key={cat.id}
                  onClick={() => setSelectedCategory(cat.id)}
                  className={`flex flex-col items-center justify-center p-3.5 rounded-2xl border transition-all text-center group ${
                    isSelected
                      ? 'bg-[#00D2FF] border-[#00D2FF] text-slate-950 font-bold shadow-lg shadow-[#00D2FF]/25 scale-[1.03]'
                      : 'bg-[#1E293B]/70 hover:bg-[#1E293B] border-slate-800 text-slate-300 hover:text-white'
                  }`}
                >
                  <div
                    className={`w-10 h-10 rounded-xl flex items-center justify-center mb-2 transition-colors ${
                      isSelected
                        ? 'bg-slate-950/20 text-slate-950'
                        : 'bg-[#0F172A] text-[#00D2FF] group-hover:scale-110'
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                  </div>
                  <span className="text-xs tracking-tight line-clamp-1">{cat.name}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Available Professionals Header */}
        <div className="flex items-center justify-between mb-5">
          <div>
            <h3 className="text-lg font-bold text-white">
              Nearby Professionals
            </h3>
            <p className="text-xs text-slate-400">
              Verified local experts available for instant booking
            </p>
          </div>
          <span className="text-xs font-bold text-slate-400 bg-[#1E293B] px-3 py-1.5 rounded-full border border-slate-800">
            {filteredProviders.length} Found
          </span>
        </div>

        {/* Providers Grid */}
        {loading ? (
          <div className="flex flex-col items-center justify-center py-16">
            <Loader2 className="w-8 h-8 text-[#00D2FF] animate-spin mb-3" />
            <p className="text-xs text-slate-400">Locating nearby service professionals from database...</p>
          </div>
        ) : filteredProviders.length === 0 ? (
          <div className="text-center py-16 bg-[#1E293B]/40 rounded-3xl border border-slate-800">
            <Wrench className="w-12 h-12 text-slate-500 mx-auto mb-3" />
            <p className="text-base font-bold text-white">No professionals found</p>
            <p className="text-xs text-slate-400 mt-1">Try selecting another category or clear your search.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {filteredProviders.map((provider) => {
              const pInitial = (provider.name ? provider.name.charAt(0) : 'P').toUpperCase();
              const category = provider.serviceCategory || provider.providerProfile?.serviceCategory || provider.category || 'Professional';
              const rate = provider.hourlyRate || provider.providerProfile?.hourlyRate || 2500;
              const bio = provider.bio || provider.providerProfile?.bio || 'Experienced and verified service professional available for immediate booking.';
              const exp = provider.experience || provider.providerProfile?.experience || '5+ yrs exp';
              const distance = provider.distance || '2.0 km away';

              return (
                <div
                  key={provider._id || provider.id}
                  className="bg-[#1E293B]/80 hover:bg-[#1E293B] border border-slate-800 hover:border-slate-700 rounded-3xl p-5 shadow-xl transition-all hover:-translate-y-1 group flex flex-col justify-between"
                >
                  <div>
                    {/* Top Info Row */}
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 rounded-2xl bg-[#00D2FF]/15 border border-[#00D2FF]/30 text-[#00D2FF] font-bold text-lg flex items-center justify-center shrink-0">
                          {pInitial}
                        </div>
                        <div>
                          <div className="flex items-center gap-1.5">
                            <h4 className="text-base font-bold text-white group-hover:text-[#00D2FF] transition-colors">
                              {provider.name}
                            </h4>
                            <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" title="Verified Pro" />
                          </div>
                          <span className="inline-block mt-0.5 text-[11px] font-semibold text-[#00D2FF] bg-[#00D2FF]/10 px-2 py-0.5 rounded-full border border-[#00D2FF]/20">
                            {category}
                          </span>
                        </div>
                      </div>

                      {/* Rating Badge */}
                      <div className="flex items-center gap-1 px-2.5 py-1 bg-[#0F172A] rounded-xl border border-slate-800">
                        <Star className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
                        <span className="text-xs font-bold text-white">{provider.rating || '4.9'}</span>
                      </div>
                    </div>

                    {/* Bio */}
                    <p className="text-xs text-slate-400 mt-3 line-clamp-2 leading-relaxed">
                      {bio}
                    </p>

                    {/* Meta row */}
                    <div className="flex items-center gap-4 mt-4 pt-4 border-t border-slate-800/80 text-xs text-slate-400">
                      <span className="flex items-center gap-1">
                        <MapPin className="w-3.5 h-3.5 text-slate-400" />
                        {distance}
                      </span>
                      <span>•</span>
                      <span>{exp}</span>
                    </div>
                  </div>

                  {/* Rate & Book Button */}
                  <div className="flex items-center justify-between mt-5 pt-3">
                    <div>
                      <span className="text-[10px] uppercase font-semibold text-slate-400">Hourly Rate</span>
                      <p className="text-base font-extrabold text-white">
                        LKR {rate}
                        <span className="text-xs text-slate-400 font-normal">/hr</span>
                      </p>
                    </div>

                    <button
                      onClick={() =>
                        setSelectedProviderForBooking({
                          ...provider,
                          serviceCategory: category,
                          hourlyRate: rate,
                        })
                      }
                      className="px-5 py-2.5 bg-[#00D2FF] hover:bg-[#00B4D8] text-slate-950 font-bold text-xs rounded-xl shadow-md shadow-[#00D2FF]/20 flex items-center gap-1.5 transition-transform active:scale-95"
                    >
                      <span>Book Now</span>
                      <ChevronRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

      </div>

      {/* Booking Modal */}
      {selectedProviderForBooking && (
        <BookingModal
          provider={selectedProviderForBooking}
          onClose={() => setSelectedProviderForBooking(null)}
          onSuccess={() => {
            setSelectedProviderForBooking(null);
            fetchProviders();
          }}
        />
      )}

    </div>
  );
}
