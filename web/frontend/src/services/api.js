/**
 * GlobalTrade Logistics API Client Service
 */

const BASE_URL = window.location.pathname.startsWith('/web') ? '/web/api' : '/api';
const TOKEN_KEY = 'gtl_access_token';
const REFRESH_TOKEN_KEY = 'gtl_refresh_token';
const USER_KEY = 'gtl_user_info';

export const getAccessToken = () => localStorage.getItem(TOKEN_KEY);
export const getRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);
export const getUser = () => {
  const data = localStorage.getItem(USER_KEY);
  return data ? JSON.parse(data) : null;
};

export const setAuthData = (data) => {
  if (data.accessToken) localStorage.setItem(TOKEN_KEY, data.accessToken);
  if (data.refreshToken) localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
  if (data.username) {
    localStorage.setItem(USER_KEY, JSON.stringify({
      username: data.username,
      role: data.role
    }));
  }
};

export const clearAuthData = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
};

export const request = async (endpoint, options = {}) => {
  const url = `${BASE_URL}${endpoint}`;
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...(options.headers || {})
  };

  const token = getAccessToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const fetchOptions = {
    ...options,
    headers,
    credentials: 'include' // Needed for Stateful Session Beans (JSESSIONID)
  };

  try {
    const response = await fetch(url, fetchOptions);

    if (response.status === 204) {
      return null;
    }

    const contentType = response.headers.get('content-type');
    let data;
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    if (!response.ok) {
      // Token expiration retry
      if (response.status === 401 && getRefreshToken()) {
        const refreshed = await tryRefreshToken();
        if (refreshed) {
          return request(endpoint, options);
        }
      }
      const message = (typeof data === 'object' && data.error)
        ? data.error
        : (typeof data === 'string' && data ? data : `Request failed with status ${response.status}`);
      const err = new Error(message);
      err.status = response.status;
      err.data = data;
      throw err;
    }

    return data;
  } catch (error) {
    throw error;
  }
};

async function tryRefreshToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return false;
  try {
    const res = await fetch(`${BASE_URL}/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken })
    });
    if (res.ok) {
      const data = await res.json();
      setAuthData(data);
      return true;
    }
  } catch (e) {
    console.error('Failed to refresh token:', e);
  }
  clearAuthData();
  return false;
}

export const api = {
  // Authentication
  auth: {
    login: (username, password) => request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    }),
    refresh: (refreshToken) => request('/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({ refreshToken })
    }),
    register: (username, password, role) => request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, role })
    }),
    getUsers: () => request('/auth/users', { method: 'GET' })
  },

  // Supply Chain Monitoring
  monitoring: {
    getStatus: () => request('/monitoring/status', { method: 'GET' }),
    getAlerts: () => request('/monitoring/alerts', { method: 'GET' }),
    acknowledgeAlert: (alertId) => request(`/monitoring/alerts/${alertId}/acknowledge`, { method: 'PUT' }),
    getMetrics: (limit = 50) => request(`/monitoring/metrics?limit=${limit}`, { method: 'GET' })
  },

  // Shipment Operations
  shipments: {
    getAll: () => request('/shipments', { method: 'GET' }),
    getByTrackingNumber: (trackingNumber) => request(`/shipments/${encodeURIComponent(trackingNumber)}`, { method: 'GET' }),
    getBySender: (username) => request(`/shipments/user/${encodeURIComponent(username)}`, { method: 'GET' }),
    create: (shipmentData) => request('/shipments', {
      method: 'POST',
      body: JSON.stringify(shipmentData)
    }),
    updateStatus: (trackingNumber, status, updatedBy) => request(`/shipments/${encodeURIComponent(trackingNumber)}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status, updatedBy })
    }),
    getDelays: () => request('/shipments/delays', { method: 'GET' })
  },

  // Stateful Shipment Booking Session Wizard
  booking: {
    start: (senderUsername, origin, destination) => request('/booking/start', {
      method: 'POST',
      body: JSON.stringify({ senderUsername, origin, destination })
    }),
    addItem: (sku, description, quantity, weightKg, declaredValue) => request('/booking/items', {
      method: 'POST',
      body: JSON.stringify({ sku, description, quantity, weightKg, declaredValue })
    }),
    removeItem: (sku) => request(`/booking/items/${encodeURIComponent(sku)}`, {
      method: 'DELETE'
    }),
    selectCarrier: (carrierCode, serviceLevel) => request('/booking/carrier', {
      method: 'POST',
      body: JSON.stringify({ carrierCode, serviceLevel })
    }),
    getSummary: () => request('/booking/summary', { method: 'GET' }),
    confirm: () => request('/booking/confirm', { method: 'POST' }),
    cancel: () => request('/booking/cancel', { method: 'POST' })
  },

  // Customs Compliance
  customs: {
    submitDeclaration: (data) => request('/customs/declarations', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
    reviewDeclaration: (declarationNumber, status, reviewedBy, notes) => request(`/customs/declarations/${encodeURIComponent(declarationNumber)}/review`, {
      method: 'PUT',
      body: JSON.stringify({ status, reviewedBy, notes })
    }),
    checkCompliance: (trackingNumber) => request(`/customs/compliance/${encodeURIComponent(trackingNumber)}`, { method: 'GET' }),
    getPending: () => request('/customs/declarations/pending', { method: 'GET' }),
    getApproachingDeadlines: (hours = 24) => request(`/customs/declarations/deadlines?hours=${hours}`, { method: 'GET' })
  },

  // Vendor Management & Evaluations
  vendors: {
    getAll: (status) => {
      const query = status ? `?status=${encodeURIComponent(status)}` : '';
      return request(`/vendors${query}`, { method: 'GET' });
    },
    register: (name, country, contactEmail) => request('/vendors', {
      method: 'POST',
      body: JSON.stringify({ name, country, contactEmail })
    }),
    evaluate: (vendorCode) => request(`/vendors/${encodeURIComponent(vendorCode)}/evaluate`, { method: 'POST' }),
    getScorecard: (vendorCode) => request(`/vendors/${encodeURIComponent(vendorCode)}/scorecard`, { method: 'GET' }),
    assign: (trackingNumber, vendorCode) => request('/vendors/assign', {
      method: 'POST',
      body: JSON.stringify({ trackingNumber, vendorCode })
    })
  },

  // Multimodal Route Optimization
  routes: {
    optimize: (origin, destination, weight, priority = 'COST') => {
      const params = new URLSearchParams({ origin, destination, weight: weight.toString(), priority });
      return request(`/routes/optimize?${params.toString()}`, { method: 'GET' });
    },
    compare: (origin, destination, weight) => {
      const params = new URLSearchParams({ origin, destination, weight: weight.toString() });
      return request(`/routes/compare?${params.toString()}`, { method: 'GET' });
    }
  },

  // Batch Operations
  batch: {
    dispatch: (items) => request('/batch/dispatch', {
      method: 'POST',
      body: JSON.stringify({ items })
    }),
    generateManifest: (trackingNumbers) => request('/batch/manifest', {
      method: 'POST',
      body: JSON.stringify(trackingNumbers)
    })
  },

  // Inventory & Warehouse Management
  inventory: {
    getAll: () => request('/inventory', { method: 'GET' }),
    getBySku: (sku) => request(`/inventory/${encodeURIComponent(sku)}`, { method: 'GET' }),
    getLowStock: () => request('/inventory/low-stock', { method: 'GET' }),
    create: (data) => request('/inventory', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
    restock: (sku, quantity) => request(`/inventory/${encodeURIComponent(sku)}/restock`, {
      method: 'POST',
      body: JSON.stringify({ quantity })
    }),
    delete: (id) => request(`/inventory/${id}`, { method: 'DELETE' })
  }
};
