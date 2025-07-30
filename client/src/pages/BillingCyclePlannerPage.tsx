import { useEffect, useMemo, useState } from 'react'
import { formatNumber } from 'accounting-js'
import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  getExpandedRowModel,
  useReactTable,
} from '@tanstack/react-table'
import {
  spendingProfileApi,
  billOptimizerApi,
  billPaymentApi,
} from '@/lib/api'
import type { SpendingProfile } from '@/lib/dataSchema'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

interface ExpenseRow {
  id: string
  amount: number
  description: string
  account: string
  profileId: string
}

interface ProfileRow {
  id: string
  name: string
  bankAccounts: string[]
  subRows: ExpenseRow[]
}

export default function BillingCyclePlannerPage() {
  const [profiles, setProfiles] = useState<ProfileRow[]>([])
  const [expanded, setExpanded] = useState<Record<string, boolean>>({})
  const [saving, setSaving] = useState(false)
  const [suggestions, setSuggestions] = useState<any[]>([])

  // load profiles
  useEffect(() => {
    spendingProfileApi
      .getAll()
      .then((res) => {
        const data = (res.data as SpendingProfile[]).map((p) => ({
          id: p.id,
          name: p.name,
          bankAccounts: p.bankAccounts,
          subRows: [],
        }))
        setProfiles(data)
      })
      .catch(() => {
        /* ignore */
      })

    billOptimizerApi
      .getSuggestions()
      .then((res) => setSuggestions(res.data))
      .catch(() => {
        /* ignore */
      })
  }, [])

  // auto save every 15s
  useEffect(() => {
    const id = setInterval(() => {
      handleSave()
    }, 15000)
    return () => clearInterval(id)
  })

  const handleSave = () => {
    setSaving(true)
    // In a real app we would send changes to backend
    setTimeout(() => setSaving(false), 500)
  }

  const addExpense = (profileId: string) => {
    setProfiles((prev) =>
      prev.map((p) =>
        p.id === profileId
          ? {
              ...p,
              subRows: [
                ...p.subRows,
                {
                  id: Math.random().toString(),
                  amount: 0,
                  description: '',
                  account: p.bankAccounts[0] || '',
                  profileId,
                },
              ],
            }
          : p,
      ),
    )
    setExpanded((e) => ({ ...e, [profileId]: true }))
  }

  const allAccounts = useMemo(
    () => {
      const set = new Set<string>()
      profiles.forEach((p) => {
        p.bankAccounts.forEach((a) => set.add(a))
      })
      return Array.from(set)
    },
    [profiles],
  )

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
          return total ? formatNumber(total, 2) : ''
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
            const total = row.original.subRows.reduce(
              (sum, e) => sum + e.amount,
              0,
            )
            return formatNumber(total, 2)
          },
        },
        {
          id: 'amount',
          header: 'Amount',
          cell: ({ row }) => {
            if (!row.depth) return null
            const expense = row.original as ExpenseRow
            const profile = profiles.find((p) => p.id === expense.profileId)
            return (
              <div className="flex gap-2 items-center">
                <select
                  className="border px-1 text-foreground"
                  value={expense.account}
                  onChange={(e) => {
                    const val = e.target.value
                    setProfiles((prev) =>
                      prev.map((p) => {
                        if (p.subRows.includes(expense)) {
                          return {
                            ...p,
                            subRows: p.subRows.map((s) =>
                              s === expense ? { ...s, account: val } : s,
                            ),
                          }
                        }
                        return p
                      }),
                    )
                  }}
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
                  onChange={(e) => {
                    const val = parseFloat(e.target.value)
                    setProfiles((prev) =>
                      prev.map((p) => {
                        if (p.subRows.includes(expense)) {
                          return {
                            ...p,
                            subRows: p.subRows.map((s) =>
                              s === expense ? { ...s, amount: val } : s,
                            ),
                          }
                        }
                        return p
                      }),
                    )
                  }}
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
            const expense = row.original as ExpenseRow
            return (
              <input
                type="text"
                className="w-full border px-1"
                value={expense.description}
                onChange={(e) => {
                  const val = e.target.value
                  setProfiles((prev) =>
                    prev.map((p) => {
                      if (p.subRows.includes(expense)) {
                        return {
                          ...p,
                          subRows: p.subRows.map((s) =>
                            s === expense ? { ...s, description: val } : s,
                          ),
                        }
                      }
                      return p
                    }),
                  )
                }}
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
    getSubRows: (row) => row.subRows,
    state: { expanded },
    onExpandedChange: setExpanded,
  })

  return (
    <div className="flex flex-col gap-6 max-w-6xl mx-auto">
      <h1 className="page-title">Billing Cycle Planner</h1>
      <Table className="border text-left">
        <TableHeader>
          {table.getHeaderGroups().map((hg) => (
            <TableRow key={hg.id}>
              {hg.headers.map((h) => (
                <TableHead key={h.id} className="text-foreground">
                  {flexRender(h.column.columnDef.header, h.getContext())}
                </TableHead>
              ))}
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
              {row.depth === 0 && (
                <TableCell>
                  <Button size="sm" onClick={() => addExpense(row.original.id)}>
                    Add Expense
                  </Button>
                </TableCell>
              )}
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <div className="flex justify-end gap-2">
        <Button onClick={handleSave} disabled={saving}>
          {saving ? 'Saving...' : 'Save'}
        </Button>
      </div>

      <div>
        <h2 className="font-bold mt-6 mb-2 text-foreground">Payment Suggestions</h2>
        <ul className="list-disc pl-6 text-foreground">
          {suggestions.map((s, idx) => (
            <li key={idx}>{JSON.stringify(s)}</li>
          ))}
        </ul>
      </div>

      <div className="flex justify-end mt-6">
        <Button
          variant="destructive"
          className="button-destructive"
          onClick={() => billPaymentApi.markComplete()}
        >
          Complete Bill Payments
        </Button>
      </div>
    </div>
  )
}
