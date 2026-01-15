import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle 401 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getCurrentUser: () => api.get('/auth/me')
};

// Suricata APIs
export const suricataAPI = {
  start: () => api.post('/suricata/start'),
  stop: () => api.post('/suricata/stop'),
  getStatus: () => api.get('/suricata/status')
};

// Alert APIs
export const alertAPI = {
  getAll: (page = 0, size = 50) => api.get(`/alerts?page=${page}&size=${size}`),
  getUnread: () => api.get('/alerts/unread'),
  getById: (id) => api.get(`/alerts/${id}`),
  markAsRead: (id) => api.put(`/alerts/${id}/read`),
  markAllAsRead: () => api.put('/alerts/read-all'),
  delete: (id) => api.delete(`/alerts/${id}`)
};

// Statistics APIs
export const statisticsAPI = {
  get: () => api.get('/statistics')
};

export default api;
