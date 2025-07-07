import {type ReactNode, useState } from 'react'
import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '@/hooks/useAuth'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

export default function AppLayout({ children }: { children?: ReactNode }) {
  const { logout } = useAuth()
  const [open, setOpen] = useState(false)

  return (
    <div className="min-h-screen flex">
      {/* mobile overlay */}
      {open && <div className="fixed inset-0 bg-black/40 z-20 sm:hidden" onClick={() => setOpen(false)} />}
      <aside
        className={cn(
          'bg-sidebar text-sidebar-foreground w-56 border-r p-4 space-y-2 z-30 transition-transform',
          'sm:translate-x-0 sm:static',
          open ? 'translate-x-0 fixed inset-y-0 left-0' : '-translate-x-full fixed inset-y-0 left-0',
        )}
      >
        <nav className="flex flex-col space-y-2">
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/credit-cards">Credit Cards</NavLink>
          <NavLink to="/expenses">Expenses</NavLink>
          <Button variant="secondary" onClick={logout} className="mt-4 text-left">Logout</Button>
        </nav>
      </aside>
      <main className="flex-1 p-4 sm:ml-56">
        {children ? children : <Outlet />}
      </main>
      <Button className="sm:hidden fixed bottom-4 left-4 z-30" onClick={() => setOpen(true)}>Menu</Button>
    </div>
  )
}
