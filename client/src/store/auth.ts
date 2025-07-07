import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthState {
  loggedIn: boolean
  setLoggedIn: (v: boolean) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      loggedIn: false,
      setLoggedIn: (v) => set({ loggedIn: v }),
    }),
    { name: 'auth' }
  )
)
