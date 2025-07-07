import { Route, Routes, Navigate } from 'react-router-dom'
import AppLayout from './components/AppLayout'
import LoginPage from './pages/LoginPage'
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
              <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
          </AppLayout>
        ) : (
          <Navigate to="/login" />
        )}
      />
    </Routes>
  )
}
