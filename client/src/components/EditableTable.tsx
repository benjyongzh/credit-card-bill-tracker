import { flexRender, Row, Table as ReactTable } from '@tanstack/react-table'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table'
import { ReactNode } from 'react'

interface Props<T> {
  table: ReactTable<T>
  renderRowAction?: (row: Row<T>) => ReactNode
}

export default function EditableTable<T>({ table, renderRowAction }: Props<T>) {
  return (
    <Table className="border text-left">
      <TableHeader>
        {table.getHeaderGroups().map((hg) => (
          <TableRow key={hg.id}>
            {hg.headers.map((h) => (
              <TableHead key={h.id} className="text-foreground">
                {flexRender(h.column.columnDef.header, h.getContext())}
              </TableHead>
            ))}
            {renderRowAction && <TableHead />}
          </TableRow>
        ))}
      </TableHeader>
      <TableBody>
        {table.getRowModel().rows.map((row) => (
          <TableRow key={row.id}>
            {row.getVisibleCells().map((cell) => (
              <TableCell key={cell.id}>
                {flexRender(cell.column.columnDef.cell, cell.getContext())}
              </TableCell>
            ))}
            {renderRowAction && <TableCell>{renderRowAction(row)}</TableCell>}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
