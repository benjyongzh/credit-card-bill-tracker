import type {ReactNode} from 'react'
import { Dialog, DialogTrigger, DialogContent } from '@/components/ui/dialog'
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
      <DialogContent className={`bg-background/30 dark:bg-background/80 backdrop-blur-xl dark:backdrop-blur ${contentClassName}`}>
        <h2 className="text-lg font-bold mb-4">{title}</h2>
        {children}
      </DialogContent>
    </Dialog>
  )
}
