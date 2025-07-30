import { Route, Routes, Navigate } from 'react-router-dom'
import AppLayout from './components/AppLayout'
import LoginPage from './pages/LoginPage'
import DashboardPage from "@/pages/DashboardPage.tsx";
import CreditCardsPage from './pages/CreditCardsPage'
import BankAccountsPage from './pages/BankAccountsPage'
import SpendingProfilesPage from './pages/SpendingProfilesPage'
import BillingCyclePlannerPage from './pages/BillingCyclePlannerPage'
import NotFoundPage from './pages/NotFoundPage'
import { useAuthStore } from './store/auth'
import {useState} from "react";

export default function App() {
  const loggedIn = useAuthStore((s) => s.loggedIn)
    const [devMode, setDevMode] = useState(true);

  return (
      <div className="relative min-h-screen min-w-screen">
          <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route
                  path="/*"
                  element={devMode ? (
                      <AppLayout>
                          <Routes>
                              <Route path="/" element={<DashboardPage />} />
                              <Route path="/credit-cards" element={<CreditCardsPage />} />
                              <Route path="/bank-accounts" element={<BankAccountsPage />} />
                              <Route path="/spending-profiles" element={<SpendingProfilesPage />} />
                              <Route path="/billing-cycle" element={<BillingCyclePlannerPage />} />
                              <Route path="/not-found" element={<NotFoundPage />} />
                              <Route path="*" element={<NotFoundPage />} />
                          </Routes>
                      </AppLayout>
                  ) : loggedIn ? (
                      <AppLayout>
                          <Routes>
                              <Route path="/" element={<DashboardPage />} />
                              <Route path="/credit-cards" element={<CreditCardsPage />} />
                              <Route path="/bank-accounts" element={<BankAccountsPage />} />
                              <Route path="/spending-profiles" element={<SpendingProfilesPage />} />
                              <Route path="/billing-cycle" element={<BillingCyclePlannerPage />} />
                              <Route path="/not-found" element={<NotFoundPage />} />
                              <Route path="*" element={<NotFoundPage />} />
                          </Routes>
                      </AppLayout>
                  ) : (
                      <Navigate to="/login" />
                  )}
              />
          </Routes>

          {/* Build tag always visible */}
          <span className="text-xs text-gray-500 absolute bottom-2 right-2">
        Build: {import.meta.env.VITE_BUILD_TAG || 'dev'}
      </span>
      </div>
  )
}
