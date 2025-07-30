import { type ReactNode, useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '@/hooks/useAuth'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { MenuIcon, XIcon } from 'lucide-react'

export default function AppLayout({ children }: { children?: ReactNode }) {
  const { logout } = useAuth()
  const [open, setOpen] = useState(false)
  const location = useLocation()

  // Determine the current page title based on the path
  const getPageTitle = () => {
    const path = location.pathname
    if (path === "/") return "Dashboard"
    if (path.includes("credit-cards")) return "Credit Cards"
    if (path.includes("bank-accounts")) return "Bank Accounts"
    if (path.includes("spending-profiles")) return "Spending Categories"
    if (path.includes("billing-cycle")) return "Billing Cycle Planner"
      if (path.includes("not-found")) return "404 - Not Found"
    return ""
  }

  return (
    <div className="min-h-screen flex flex-col">
      {/* mobile overlay */}
      {open && (
        <div className="fixed inset-0 bg-black/50 blur-3xs backdrop-blur-xs duration-200 z-20 sm:hidden" onClick={() => setOpen(false)} />
      )}

      {/* mobile sidebar */}
        <aside
            className={cn(
                'bg-background dark:bg-background/80 text-foreground w-72 p-4 z-30 transition-transform sm:hidden',
                open ? 'translate-x-0 fixed inset-y-0 left-0' : '-translate-x-full fixed inset-y-0 left-0',
                'flex flex-col' // make aside a flex column container
            )}
        >
            <Button
                variant="ghost"
                size="icon"
                onClick={() => setOpen(false)}
                className="h-8 w-8 mb-10"
            >
                <XIcon />
            </Button>

            <nav className="flex flex-col gap-4 flex-1">
                <NavLink className="font-bold" to="/credit-cards">Cards</NavLink>
                <NavLink className="font-bold" to="/bank-accounts">Accounts</NavLink>
                <NavLink className="font-bold" to="/spending-profiles">Categories</NavLink>
                <NavLink className="font-bold" to="/billing-cycle">Cycle Planner</NavLink>
            </nav>

            <Button
                variant="destructive"
                onClick={logout}
                className="text-left mt-auto button-destructive"
            >
                Logout
            </Button>
        </aside>

      {/* desktop navbar */}
      <header className="hidden sm:flex items-center justify-end gap-6 text-foreground p-4 border-b border-muted">
        <nav className="flex items-center space-x-8">
          <NavLink className="fixed left-5 text-foreground font-bold" to="/">Credit Card Bill Tracker</NavLink>
          <NavLink className="font-bold" to="/credit-cards">Cards</NavLink>
          <NavLink className="font-bold" to="/bank-accounts">Accounts</NavLink>
          <NavLink className="font-bold" to="/spending-profiles">Categories</NavLink>
          <NavLink className="font-bold" to="/billing-cycle">Cycle Planner</NavLink>
        </nav>
        <Button variant="destructive" className="button-destructive" onClick={logout}>Logout</Button>
      </header>

      <main className="flex-1 p-4">
          {/* Page content */}
          <div className="sm:hidden flex items-center mb-4">
            <Button
              variant="ghost"
              size="icon"
              className="mr-2 text-sidebar-foreground"
              onClick={() => setOpen(true)}
            >
              <MenuIcon/>
            </Button>
            <h1 className="text-2xl text-foreground font-semibold">
              {/* Display the current page title */}
              {getPageTitle()}
            </h1>
              <NavLink className="fixed right-5 text-foreground" to="/">Home</NavLink>
          </div>
          {children ? children : <Outlet />}
      </main>


    </div>
  )
}
