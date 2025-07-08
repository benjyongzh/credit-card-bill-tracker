import {type FormEvent, useState } from 'react'
import { useAuth } from '@/hooks/useAuth'

export default function LoginPage() {
  const { login } = useAuth()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const googleLogin = () => {
    const base = import.meta.env.VITE_API_BASE_URL.replace(/\/api$/, '')
    window.location.href = `${base}${import.meta.env.VITE_OAUTH_LOGIN_CALLBACK_URL}`
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      await login(username, password)
    } catch (err) {
      setError('Login failed')
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen p-4">
      <form onSubmit={onSubmit} className="space-y-4 w-80">
        <h1 className="text-xl font-bold text-center">Login</h1>
        {error && <p className="text-red-500">{error}</p>}
        <div>
          <label className="block mb-1" htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full border p-2 rounded"
            required
          />
        </div>
        <div>
          <label className="block mb-1" htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full border p-2 rounded"
            required
          />
        </div>
        <button type="submit" className="w-full bg-blue-600 text-white p-2 rounded">
          Login
        </button>
        <button type="button" onClick={googleLogin} className="w-full bg-red-600 text-white p-2 rounded">
          Sign in with Google
        </button>
      </form>
    </div>
  )
}
