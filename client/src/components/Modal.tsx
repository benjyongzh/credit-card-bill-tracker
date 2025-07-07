import { ReactNode } from 'react'
import { Dialog, DialogTrigger, DialogContent } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'

interface ModalProps {
  title: string
  triggerLabel: string
  children: ReactNode
  triggerClassName?: string
  contentClassName?: string
}

export default function Modal({
  title,
  triggerLabel,
  children,
  triggerClassName,
  contentClassName,
}: ModalProps) {
  return (
    <Dialog>
      <DialogTrigger>
        <Button className={triggerClassName}>{triggerLabel}</Button>
      </DialogTrigger>
      <DialogContent className={contentClassName}>
        <h2 className="text-lg font-bold mb-4">{title}</h2>
        {children}
      </DialogContent>
    </Dialog>
  )
}
