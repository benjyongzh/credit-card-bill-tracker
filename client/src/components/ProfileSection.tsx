import { useMemo } from 'react'
import { formatNumber } from 'accounting-js'
import type { ColumnDef } from '@tanstack/react-table'
import { getCoreRowModel, getExpandedRowModel, useReactTable } from '@tanstack/react-table'
import EditableTable from './EditableTable'
import { Button } from './ui/button'
import type { ProfileRow, ExpenseRow } from '@/hooks/useProfiles'
import { useProfiles } from '@/hooks/useProfiles'

export default function ProfileSection() {
  const { profiles, expanded, setExpanded, addExpense, updateExpense, allAccounts } = useProfiles()

  const columns = useMemo<ColumnDef<ProfileRow>[]>(
    () => {
      const accountCols: ColumnDef<ProfileRow>[] = allAccounts.map((acc) => ({
        id: acc,
        header: acc,
        cell: ({ row }) => {
          if (row.depth !== 0) return null
          const total = row.original.subRows
            .filter((e) => e.account === acc)
            .reduce((sum, e) => sum + e.amount, 0)
          return total ? formatNumber(total, { precision: 2 }) : ''
        },
      }))

      return [
        {
          id: 'name',
          header: 'Category',
          cell: ({ row }) => {
            const isExpanded = row.getIsExpanded()
            return (
              <div className="flex items-center gap-2">
                {row.getCanExpand() && (
                  <button
                    type="button"
                    onClick={row.getToggleExpandedHandler()}
                    className="font-mono"
                  >
                    {isExpanded ? '-' : '+'}
                  </button>
                )}
                {row.depth === 0 && (
                  <span className="font-semibold text-foreground">
                    {(row.original as ProfileRow).name}
                  </span>
                )}
              </div>
            )
          },
        },
        ...accountCols,
        {
          id: 'total',
          header: 'Total',
          cell: ({ row }) => {
            if (row.depth !== 0) return null
            const total = row.original.subRows.reduce((sum, e) => sum + e.amount, 0)
            return formatNumber(total, { precision: 2 })
          },
        },
        {
          id: 'amount',
          header: 'Amount',
          cell: ({ row }) => {
            if (!row.depth) return null
            const expense = row.original as unknown as ExpenseRow
            const profile = profiles.find((p) => p.id === expense.profileId)
            return (
              <div className="flex gap-2 items-center">
                <select
                  className="border px-1 text-foreground"
                  value={expense.account}
                  onChange={(e) => updateExpense(expense, { account: e.target.value })}
                >
                  {profile?.bankAccounts.map((a) => (
                    <option key={a} value={a}>
                      {a}
                    </option>
                  ))}
                </select>
                <input
                  type="number"
                  className="w-24 border px-1"
                  value={expense.amount}
                  onChange={(e) => updateExpense(expense, { amount: parseFloat(e.target.value) })}
                />
              </div>
            )
          },
        },
        {
          id: 'description',
          header: 'Description',
          cell: ({ row }) => {
            if (!row.depth) return null
            const expense = row.original as unknown as ExpenseRow
            return (
              <input
                type="text"
                className="w-full border px-1"
                value={expense.description}
                onChange={(e) => updateExpense(expense, { description: e.target.value })}
              />
            )
          },
        },
      ]
    },
    [allAccounts, profiles],
  )

  const table = useReactTable({
    data: profiles,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getExpandedRowModel: getExpandedRowModel(),
    getSubRows: (row) => row.subRows as unknown as ProfileRow[],
    state: { expanded },
    onExpandedChange: setExpanded as any,
  })

  return (
    <EditableTable
      table={table}
      renderRowAction={(row) =>
        row.depth === 0 ? (
          <Button size="sm" onClick={() => addExpense(row.original.id)}>
            Add Expense
          </Button>
        ) : null
      }
    />
  )
}
