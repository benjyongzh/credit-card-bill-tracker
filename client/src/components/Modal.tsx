import type {ReactNode} from 'react'
import {Dialog, DialogTrigger, DialogContent, DialogTitle} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'

interface ModalProps {
  title: string
  triggerLabel: string
  children: ReactNode
  triggerClassName?: string
  contentClassName?: string
  onOpen?: () => void
}

export default function Modal({
  title,
  triggerLabel,
  children,
  triggerClassName,
  contentClassName,
  onOpen,
}: ModalProps) {
  return (
    <Dialog>
      <DialogTrigger onClick={onOpen}>
        <Button className={triggerClassName}>{triggerLabel}</Button>
      </DialogTrigger>
      <DialogContent className={`bg-background/90 dark:bg-primary/40 backdrop-blur-xl dark:backdrop-blur ${contentClassName}`}>
        <DialogTitle className="text-foreground mb-4">{title}</DialogTitle>
        {children}
      </DialogContent>
    </Dialog>
  )
}
