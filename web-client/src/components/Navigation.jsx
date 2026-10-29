import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { Home, Calendar, User, Briefcase, LogOut, Wrench } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Navigation() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const isCustomer = user.role !== 'provider';
  const initial = (user.name ? user.name.charAt(0) : 'U').toUpperCase();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      {/* Top Desktop / Tablet Navigation Bar */}
      <header className="sticky top-0 z-50 backdrop-blur-xl bg-[#0F172A]/85 border-b border-slate-800/80">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          
          {/* Logo & Brand */}
          <NavLink to="/" className="flex items-center gap-3 group">
            <div className="relative">
              <img
                src="/localpro_logo.jpg"
                alt="LocalPro Logo"
                className="w-10 h-10 rounded-xl object-cover ring-2 ring-[#00D2FF]/40 shadow-lg shadow-[#00D2FF]/20 group-hover:scale-105 transition-transform"
              />
              <span className="absolute -bottom-1 -right-1 w-3.5 h-3.5 bg-emerald-500 border-2 border-[#0F172A] rounded-full"></span>
            </div>
            <div>
              <span className="text-xl font-bold tracking-tight text-white flex items-center gap-1.5">
                Local<span className="text-[#00D2FF]">Pro</span>
              </span>
              <p className="text-[10px] uppercase font-semibold tracking-wider text-slate-400 -mt-1">
                {isCustomer ? 'Customer Portal' : 'Provider Hub'}
              </p>
            </div>
          </NavLink>

          {/* Desktop Nav Links */}
          <nav className="hidden md:flex items-center gap-1 bg-[#1E293B]/60 p-1.5 rounded-full border border-slate-800">
            {isCustomer ? (
              <>
                <NavLink
                  to="/"
                  end
                  className={({ isActive }) =>
                    `flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/25 font-semibold'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/50'
                    }`
                  }
                >
                  <Home className="w-4 h-4" />
                  <span>Home</span>
                </NavLink>

                <NavLink
                  to="/bookings"
                  className={({ isActive }) =>
                    `flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/25 font-semibold'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/50'
                    }`
                  }
                >
                  <Calendar className="w-4 h-4" />
                  <span>My Bookings</span>
                </NavLink>

                <NavLink
                  to="/profile"
                  className={({ isActive }) =>
                    `flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/25 font-semibold'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/50'
                    }`
                  }
                >
                  <User className="w-4 h-4" />
                  <span>Profile</span>
                </NavLink>
              </>
            ) : (
              <>
                <NavLink
                  to="/provider"
                  end
                  className={({ isActive }) =>
                    `flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/25 font-semibold'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/50'
                    }`
                  }
                >
                  <Briefcase className="w-4 h-4" />
                  <span>Jobs & Requests</span>
                </NavLink>

                <NavLink
                  to="/profile"
                  className={({ isActive }) =>
                    `flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-[#00D2FF] text-slate-950 shadow-md shadow-[#00D2FF]/25 font-semibold'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/50'
                    }`
                  }
                >
                  <User className="w-4 h-4" />
                  <span>Profile</span>
                </NavLink>
              </>
            )}
          </nav>

          {/* User Profile / Logout Button */}
          <div className="flex items-center gap-3">
            <NavLink
              to="/profile"
              className="flex items-center gap-2.5 p-1.5 pl-2.5 pr-3 rounded-full bg-[#1E293B] border border-slate-700 hover:border-[#00D2FF]/50 transition-colors"
            >
              <div className="w-8 h-8 rounded-full bg-[#00D2FF]/20 border border-[#00D2FF]/40 text-[#00D2FF] font-bold text-sm flex items-center justify-center">
                {initial}
              </div>
              <span className="hidden sm:inline text-xs font-semibold text-slate-200">
                {user.name || 'User'}
              </span>
            </NavLink>

            <button
              onClick={handleLogout}
              title="Logout"
              className="p-2 rounded-full bg-rose-500/10 text-rose-400 hover:bg-rose-500/20 border border-rose-500/30 transition-colors"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>

        </div>
      </header>

      {/* Mobile Bottom Navigation Bar (Matches Android BottomNavigationView) */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-[#0F172A]/95 backdrop-blur-xl border-t border-slate-800 px-6 py-2.5 flex items-center justify-around shadow-2xl">
        {isCustomer ? (
          <>
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                `flex flex-col items-center gap-1 transition-all ${
                  isActive ? 'text-[#00D2FF] scale-105 font-semibold' : 'text-slate-400 hover:text-slate-200'
                }`
              }
            >
              <Home className="w-5 h-5" />
              <span className="text-[11px]">Home</span>
            </NavLink>

            <NavLink
              to="/bookings"
              className={({ isActive }) =>
                `flex flex-col items-center gap-1 transition-all ${
                  isActive ? 'text-[#00D2FF] scale-105 font-semibold' : 'text-slate-400 hover:text-slate-200'
                }`
              }
            >
              <Calendar className="w-5 h-5" />
              <span className="text-[11px]">Bookings</span>
            </NavLink>

            <NavLink
              to="/profile"
              className={({ isActive }) =>
                `flex flex-col items-center gap-1 transition-all ${
                  isActive ? 'text-[#00D2FF] scale-105 font-semibold' : 'text-slate-400 hover:text-slate-200'
                }`
              }
            >
              <User className="w-5 h-5" />
              <span className="text-[11px]">Profile</span>
            </NavLink>
          </>
        ) : (
          <>
            <NavLink
              to="/provider"
              end
              className={({ isActive }) =>
                `flex flex-col items-center gap-1 transition-all ${
                  isActive ? 'text-[#00D2FF] scale-105 font-semibold' : 'text-slate-400 hover:text-slate-200'
                }`
              }
            >
              <Briefcase className="w-5 h-5" />
              <span className="text-[11px]">Dashboard</span>
            </NavLink>

            <NavLink
              to="/profile"
              className={({ isActive }) =>
                `flex flex-col items-center gap-1 transition-all ${
                  isActive ? 'text-[#00D2FF] scale-105 font-semibold' : 'text-slate-400 hover:text-slate-200'
                }`
              }
            >
              <User className="w-5 h-5" />
              <span className="text-[11px]">Profile</span>
            </NavLink>
          </>
        )}
      </nav>
    </>
  );
}
