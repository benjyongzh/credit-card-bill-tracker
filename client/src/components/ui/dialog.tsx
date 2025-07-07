import * as React from 'react'
import { cn } from '@/lib/utils'

export interface DialogProps extends React.HTMLAttributes<HTMLDivElement> {
  open?: boolean
  onOpenChange?: (open: boolean) => void
}

interface Ctx {
  open: boolean
  setOpen: (o: boolean) => void
}

const DialogContext = React.createContext<Ctx | null>(null)

function Dialog({ open = false, onOpenChange, ...props }: DialogProps) {
  const [isOpen, setIsOpen] = React.useState(open)

  const setOpen = (o: boolean) => {
    setIsOpen(o)
    onOpenChange?.(o)
  }

  return (
    <DialogContext.Provider value={{ open: isOpen, setOpen }}>
      {props.children}
    </DialogContext.Provider>
  )
}

function DialogTrigger({ children, className, onClick }: { children: React.ReactNode; className?: string; onClick?: () => void }) {
  const ctx = React.useContext(DialogContext)
  if (!ctx) throw new Error('DialogTrigger must be used within Dialog')
  return (
    <div
      onClick={() => {
        onClick?.()
        ctx.setOpen(true)
      }}
      className={className}
    >
      {children}
    </div>
  )
}

function DialogContent({ children, className }: { children: React.ReactNode; className?: string }) {
  const ctx = React.useContext(DialogContext)
  if (!ctx) return null
  if (!ctx.open) return null
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={() => ctx.setOpen(false)}>
      <div className={cn('bg-background p-4 rounded shadow w-96', className)} onClick={(e) => e.stopPropagation()}>
        {children}
      </div>
    </div>
  )
}

function useDialog() {
  const ctx = React.useContext(DialogContext)
  if (!ctx) throw new Error('useDialog must be used within Dialog')
  return ctx
}

function DialogClose({ children, className }: { children: React.ReactNode; className?: string }) {
  const { setOpen } = useDialog()
  return (
    <div onClick={() => setOpen(false)} className={className}>
      {children}
    </div>
  )
}

export { Dialog, DialogTrigger, DialogContent, DialogClose, useDialog }
