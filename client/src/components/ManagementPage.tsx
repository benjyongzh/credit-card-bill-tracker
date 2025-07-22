import {type ReactNode, useState } from 'react'
import { useEntityList } from '@/hooks/useEntityList'
import ModalForm from './ModalForm'
import { Button } from './ui/button'
import { ZodObject, type ZodRawShape } from 'zod'
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
  titleShortForm: string
  subtitle?: string
  endpoint: string
  columns: Column<T>[]
  formSchema: ZodObject<ZodRawShape>
  defaultValues?: Record<string, any>
  renderForm: (item: T | null, form?: any) => ReactNode
}

export default function ManagementPage<T extends { id: string | number }>({
  title,
  titleShortForm,
  subtitle,
  endpoint,
  columns,
  formSchema,
  defaultValues,
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
    <div className="flex flex-col gap-4 max-w-6xl mx-auto">
      <div className="flex items-center justify-between w-full mt-6">
        <h1 className="page-title">{title}</h1>
        <div className="w-full sm:w-auto flex justify-center items-center">
          <ModalForm
              title={`Add ${title}`}
              triggerLabel={`Add ${titleShortForm}`}
              triggerClassName="w-full sm:w-auto button-primary"
              formSchema={formSchema}
              defaultValues={defaultValues}
              onSubmit={(data) => handleCreate(data as Partial<T>)}
          >
            {(form) => renderForm(null, form)}
          </ModalForm>
        </div>
      </div>
      {subtitle && (<div className="hidden sm:flex text-muted-foreground">{subtitle}</div>)}


      <Table className="border text-left mt-4">
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
                  formSchema={formSchema}
                  defaultValues={{ ...defaultValues, ...item }}
                  onOpen={() => setEditing(item)}
                  onSubmit={(data) => handleUpdate(data as Partial<T>)}
                  triggerClassName="mr-2"
                >
                  {(form) => renderForm(item, form)}
                </ModalForm>
                <Button variant="destructive" onClick={() => remove(item.id)}>
                  Delete
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
