import {type ReactNode, useState } from 'react'
import AppLayout from './AppLayout'
import { useEntityList } from '@/hooks/useEntityList'
import ModalForm from './ModalForm'
import { Button } from './ui/button'
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from './ui/table'

export interface Column<T> {
  key: keyof T
  header: string
  render?: (item: T) => ReactNode
}

interface Props<T extends { id: string | number }> {
  title: string
  endpoint: string
  columns: Column<T>[]
  renderForm: (item: T | null) => ReactNode
}

export default function ManagementPage<T extends { id: string | number }>({
  title,
  endpoint,
  columns,
  renderForm,
}: Props<T>) {
  const { items, create, update, remove } = useEntityList<T>(endpoint)
  const [editing, setEditing] = useState<T | null>(null)

  const handleCreate = async (data: Partial<T>) => {
    await create(data)
  }

  const handleUpdate = async (data: Partial<T>) => {
    if (editing) {
      await update(editing.id, data)
      setEditing(null)
    }
  }

  return (
    <AppLayout>
      <h1 className="text-2xl font-bold mb-4">{title}</h1>
      <ModalForm
        title={`Add ${title}`}
        triggerLabel="Add"
        onSubmit={(data) => handleCreate(data as Partial<T>)}
      >
        {renderForm(null)}
      </ModalForm>
      <Table className="border mt-4 text-left">
        <TableHeader>
          <TableRow>
            {columns.map((c) => (
              <TableHead key={String(c.key)}>{c.header}</TableHead>
            ))}
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {items.map((item) => (
            <TableRow key={item.id}>
              {columns.map((c) => (
                <TableCell key={String(c.key)}>
                  {c.render ? c.render(item) : (item as any)[c.key]}
                </TableCell>
              ))}
              <TableCell className="space-x-2">
                <ModalForm
                  title="Edit"
                  triggerLabel="Edit"
                  onOpen={() => setEditing(item)}
                  onSubmit={(data) => handleUpdate(data as Partial<T>)}
                  triggerClassName="mr-2"
                >
                  {renderForm(item)}
                </ModalForm>
                <Button variant="secondary" onClick={() => remove(item.id)}>
                  Delete
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </AppLayout>
  )
}
