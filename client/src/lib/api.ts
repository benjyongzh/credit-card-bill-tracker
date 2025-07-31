import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      window.location.assign('/404')
    }
    return Promise.reject(error)
  },
)

// -------- Authentication --------
export const authApi = {
  login: (username: string, password: string) =>
    api.post('/auth/login', { username, password }),
  logout: () => api.post('/auth/logout'),
}

// -------- User --------
export const userApi = {
  register: (payload: unknown) => api.post('/users/register', payload),
  getProfile: (id?: string) => api.get(`/users${id ? `/${id}` : ''}`),
}

// -------- Credit Cards --------
export const creditCardApi = {
  getAll: () => api.get('/cards'),
  create: (payload: unknown) => api.post('/cards', payload),
  update: (id: string, payload: unknown) => api.put(`/cards/${id}`, payload),
  remove: (id: string) => api.delete(`/cards/${id}`),
}

// -------- Bank Accounts --------
export const bankAccountApi = {
  getAll: () => api.get('/accounts'),
  create: (payload: unknown) => api.post('/accounts', payload),
  update: (id: string, payload: unknown) =>
    api.put(`/accounts/${id}`, payload),
  remove: (id: string) => api.delete(`/accounts/${id}`),
}

// -------- Spending Profiles --------
export const spendingProfileApi = {
  getAll: () => api.get('/spending-profiles'),
  create: (payload: unknown) => api.post('/spending-profiles', payload),
  update: (id: string, payload: unknown) =>
    api.put(`/spending-profiles/${id}`, payload),
  remove: (id: string) => api.delete(`/spending-profiles/${id}`),
}

// -------- Expenses --------
export const expenseApi = {
  getAll: (cardId?: string) =>
    api.get('/expenses', { params: { cardId } }),
  create: (payload: unknown, spendingProfileId: string) =>
    api.post('/expenses', payload, { params: { spendingProfileId } }),
  update: (id: string, payload: unknown) => api.put(`/expenses/${id}`, payload),
  remove: (id: string) => api.delete(`/expenses/${id}`),
}

// -------- Billing Cycles --------
export const billingCycleApi = {
  getAll: () => api.get('/billing-cycles'),
  getById: (id: string) => api.get(`/billing-cycles/${id}`),
  create: (payload: unknown) => api.post('/billing-cycles', payload),
  update: (id: string, payload: unknown) =>
    api.put(`/billing-cycles/${id}`, payload),
  remove: (id: string) => api.delete(`/billing-cycles/${id}`),
  getDeferredBills: (id: string) =>
    api.get(`/billing-cycles/${id}/deferred-bills`),
  complete: (id: string) => api.post(`/billing-cycles/${id}/complete`),
}

// -------- Bill Payments --------
export const billPaymentApi = {
  getAll: () => api.get('/bills'),
  create: (payload: unknown) => api.post('/bills', payload),
  update: (id: string, payload: unknown) => api.put(`/bills/${id}`, payload),
  remove: (id: string) => api.delete(`/bills/${id}`),
}

// -------- Bill Optimizer --------
export const billOptimizerApi = {
  getSuggestions: (strategy?: string) =>
    api.get('/bills/optimizer', { params: { strategy } }),
}

// -------- Expense Summaries --------
export const expenseSummaryApi = {
  getFromUser: (id: string) => api.get(`/expense-summaries/${id}`),
}

export default api
