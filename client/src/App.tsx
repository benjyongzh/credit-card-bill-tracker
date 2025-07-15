import { Route, Routes, Navigate } from 'react-router-dom'
import AppLayout from './components/AppLayout'
import LoginPage from './pages/LoginPage'
import CreditCardsPage from './pages/CreditCardsPage'
import BankAccountsPage from './pages/BankAccountsPage'
import SpendingProfilesPage from './pages/SpendingProfilesPage'
import NotFoundPage from './pages/NotFoundPage'
import { useAuthStore } from './store/auth'

function Dashboard() {
  return <div>Welcome to your dashboard</div>
}

export default function App() {
  const loggedIn = useAuthStore((s) => s.loggedIn)

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/*"
        element={loggedIn ? (
          <AppLayout>
            <Routes>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/credit-cards" element={<CreditCardsPage />} />
              <Route path="/bank-accounts" element={<BankAccountsPage />} />
              <Route path="/spending-profiles" element={<SpendingProfilesPage />} />
              <Route path="/404" element={<NotFoundPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </AppLayout>
        ) : (
          <Navigate to="/login" />
        )}
      />
    </Routes>
  )
}
