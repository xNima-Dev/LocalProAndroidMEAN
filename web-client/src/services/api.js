// LocalPro API Service layer (Native Fetch with JWT Bearer Token)

// Using relative root so Vite proxies to backend seamlessly on both Desktop & Phone!
const BASE_URL = '';

function getAuthHeaders() {
  const token = localStorage.getItem('auth_token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function handleResponse(response) {
  const contentType = response.headers.get('content-type');
  const data = contentType && contentType.includes('application/json')
    ? await response.json()
    : { message: await response.text() };

  if (!response.ok) {
    const errorMsg = data.message || data.error || `Error ${response.status}: ${response.statusText}`;
    throw new Error(errorMsg);
  }
  return data;
}

export const authAPI = {
  login: async (email, password) => {
    const res = await fetch(`${BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    return handleResponse(res);
  },

  register: async (userData) => {
    const payload = {
      name: userData.name,
      email: userData.email,
      password: userData.password,
      role: userData.role,
      phoneNumber: userData.phone || userData.phoneNumber,
    };
    const res = await fetch(`${BASE_URL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  getProfile: async () => {
    const res = await fetch(`${BASE_URL}/api/auth/profile`, {
      headers: getAuthHeaders(),
    });
    return handleResponse(res);
  },

  updateProfile: async (profileData) => {
    const res = await fetch(`${BASE_URL}/api/auth/profile`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(profileData),
    });
    return handleResponse(res);
  },

  getNearProviders: async (category = '') => {
    // Default to Colombo coordinates
    const lat = 6.9271;
    const lng = 79.8612;
    const query = new URLSearchParams({
      latitude: lat,
      longitude: lng,
      ...(category && category !== 'All' ? { category } : {}),
    });

    const res = await fetch(`${BASE_URL}/api/auth/near-providers?${query.toString()}`, {
      headers: getAuthHeaders(),
    });
    return handleResponse(res);
  },
};

export const bookingAPI = {
  createBooking: async (bookingData) => {
    const payload = {
      providerId: bookingData.providerId,
      serviceCategory: bookingData.serviceCategory,
      jobDescription: bookingData.notes || 'Service requested via Web Portal',
      distanceText: bookingData.distanceText || '2.0 km',
      estimatedEarning: Number(bookingData.totalAmount) || 2500,
      customerLat: 6.9271,
      customerLon: 79.8612,
    };

    const res = await fetch(`${BASE_URL}/api/auth/bookings`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  getCustomerBookings: async () => {
    const res = await fetch(`${BASE_URL}/api/auth/bookings/customer`, {
      headers: getAuthHeaders(),
    });
    return handleResponse(res);
  },

  getProviderBookings: async (type = 'pending') => {
    let endpoint = `${BASE_URL}/api/auth/bookings`;
    if (type === 'pending') endpoint = `${BASE_URL}/api/auth/bookings/pending`;
    else if (type === 'active') endpoint = `${BASE_URL}/api/auth/bookings/active`;
    else if (type === 'completed') endpoint = `${BASE_URL}/api/auth/bookings/completed`;

    const res = await fetch(endpoint, {
      headers: getAuthHeaders(),
    });
    return handleResponse(res);
  },

  updateStatus: async (bookingId, action) => {
    // action: 'accept' | 'decline' | 'ride' | 'arrived' | 'complete' | 'paid'
    const res = await fetch(`${BASE_URL}/api/auth/bookings/${bookingId}/${action}`, {
      method: 'POST',
      headers: getAuthHeaders(),
    });
    return handleResponse(res);
  },
};
