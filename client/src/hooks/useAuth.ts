import { useNavigate } from 'react-router-dom'
import api from '@/lib/api'
import { useAuthStore } from '@/store/auth'

export function useAuth() {
  const navigate = useNavigate()
  const setLoggedIn = useAuthStore((s) => s.setLoggedIn)

  const login = async (username: string, password: string) => {
    await api.post('/auth/login', { username, password })
    setLoggedIn(true)
    navigate('/dashboard')
  }

  const logout = async () => {
    await api.post('/auth/logout')
    setLoggedIn(false)
    navigate('/login')
  }

  return { login, logout }
}
