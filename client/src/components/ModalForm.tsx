import {type ReactNode, useRef } from 'react'
import Modal from './Modal'
import { DialogClose } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'

interface ModalFormProps {
  title: string
  triggerLabel: string
  onSubmit: (data: Record<string, FormDataEntryValue>) => void
  children: ReactNode
  triggerClassName?: string
  contentClassName?: string
  onOpen?: () => void
}

export default function ModalForm({
  title,
  triggerLabel,
  onSubmit,
  children,
  onOpen,
  ...rest
}: ModalFormProps) {
  return (
    <Modal title={title} triggerLabel={triggerLabel} onOpen={onOpen} {...rest}>
      <FormBody onSubmit={onSubmit}>{children}</FormBody>
    </Modal>
  )
}

function FormBody({ onSubmit, children }: { onSubmit: (data: Record<string, FormDataEntryValue>) => void; children: ReactNode }) {
  const closeRef = useRef<HTMLButtonElement>(null);

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault()
        const formData = Object.fromEntries(new FormData(e.currentTarget).entries())
        onSubmit(formData)
        // Close the modal after form submission
        closeRef.current?.click();
      }}
      className="space-y-4"
    >
      {children}
      <div className="flex justify-end space-x-2">
        <DialogClose asChild>
          <Button variant="secondary">
            Cancel
          </Button>
        </DialogClose>
        <Button type="submit">Save</Button>
        <DialogClose ref={closeRef} className="hidden" aria-hidden="true" />
      </div>
    </form>
  )
}
