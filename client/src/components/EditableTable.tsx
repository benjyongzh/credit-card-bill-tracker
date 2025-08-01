import { ReactNode } from 'react'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { TrashIcon } from 'lucide-react'

export interface EditableRow {
  id: string
  serverId?: string
  dirty?: boolean
}

interface ColumnConfig<Row> {
  header: ReactNode
  render: (
    row: Row,
    update: (patch: Partial<Row>) => void,
    onKeyDown: (e: React.KeyboardEvent) => void,
  ) => ReactNode
}

interface EditableTableProps<Row extends EditableRow> {
  rows: Row[]
  setRows: React.Dispatch<React.SetStateAction<Row[]>>
  columns: ColumnConfig<Row>[]
  makeRow: () => Row
  addLabel: string
  onDeleteRow?: (row: Row) => void
}

export default function EditableTable<Row extends EditableRow>({
  rows,
  setRows,
  columns,
  makeRow,
  addLabel,
  onDeleteRow,
}: EditableTableProps<Row>) {
  const addRow = (afterId?: string) => {
    const newRow = makeRow()
    setRows((prev) => {
      if (!afterId) return [...prev, newRow]
      const idx = prev.findIndex((r) => r.id === afterId)
      if (idx === -1) return [...prev, newRow]
      return [...prev.slice(0, idx + 1), newRow, ...prev.slice(idx + 1)]
    })
  }

  const updateRow = (row: Row, patch: Partial<Row>) => {
    setRows((prev) => prev.map((r) => (r === row ? { ...r, ...patch, dirty: true } : r)))
  }

  const deleteRow = (row: Row) => {
    setRows((prev) => prev.filter((r) => r !== row))
    if (onDeleteRow && row.serverId) onDeleteRow(row)
  }

  const keyHandler = (id: string) => (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') addRow(id)
  }

  return (
    <>
      <Table className="border text-left">
        <TableHeader>
          <TableRow>
            {columns.map((c, i) => (
              <TableHead key={i} className="text-foreground">
                {c.header}
              </TableHead>
            ))}
            <TableHead />
          </TableRow>
        </TableHeader>
        <TableBody>
          {rows.map((row) => (
            <TableRow key={row.id} className="group">
              {columns.map((c, i) => (
                <TableCell key={i}>
                  {c.render(row, (patch) => updateRow(row, patch), keyHandler(row.id))}
                </TableCell>
              ))}
              <TableCell className="w-4 text-right">
                <Button
                  size="icon"
                  variant="ghost"
                  className="text-destructive opacity-0 group-hover:opacity-100"
                  onClick={() => deleteRow(row)}
                >
                  <TrashIcon className="size-4" />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <div className="mt-2">
        <Button size="sm" onClick={() => addRow()}>
          {addLabel}
        </Button>
      </div>
    </>
  )
}
