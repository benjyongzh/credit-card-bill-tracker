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
  billingCycleApi,
  creditCardApi,
  bankAccountApi,
  expenseApi,
  billPaymentApi,
} from '@/lib/api'
import type {
  SpendingProfile,
  BillingCycle,
  Card,
  BankAccount,
} from '@/lib/dataSchema'
import dayjs from 'dayjs'
import { Button } from '@/components/ui/button'
import Modal from '@/components/Modal'
import { DialogClose } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
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
  const [currentCycle, setCurrentCycle] = useState<BillingCycle | null>(null)
  const [cycles, setCycles] = useState<BillingCycle[]>([])
  const [accounts, setAccounts] = useState<BankAccount[]>([])
  const [cards, setCards] = useState<Card[]>([])
  const [createOpen, setCreateOpen] = useState(false)
  const [labelInput, setLabelInput] = useState('')
  const [monthInput, setMonthInput] = useState(dayjs().format('MMMM').toUpperCase())

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

    billingCycleApi
      .getAll()
      .then((res) => {
        const cycles = res.data as BillingCycle[]
        setCycles(cycles)
        if (cycles.length) {
          const latest = cycles
            .slice()
            .sort((a, b) => dayjs(a.updatedAt).diff(dayjs(b.updatedAt)))
            .at(-1)!
          setCurrentCycle(latest)
          setLabelInput(latest.label)
          setMonthInput(latest.month)
        }
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

    creditCardApi
      .getAll()
      .then((res) => setCards(res.data as Card[]))
      .catch(() => {
        /* ignore */
      })

    bankAccountApi
      .getAll()
      .then((res) => setAccounts(res.data as BankAccount[]))
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

  const handleSave = async () => {
    if (!currentCycle) return
    setSaving(true)
    const accountMap: Record<string, string> = {}
    accounts.forEach((a) => {
      accountMap[a.name] = a.id
    })

    const promises: Promise<any>[] = []
    profiles.forEach((p) => {
      p.subRows.forEach((e) => {
        const accId = accountMap[e.account]
        if (!accId || !cards[0]) return
        const payload = {
          creditCardId: cards[0].id,
          date: dayjs().format('YYYY-MM-DD'),
          amount: e.amount,
          description: e.description,
          bankAccountIds: [accId],
          billingCycleId: currentCycle.id,
        }
        promises.push(expenseApi.create(payload, p.id))
      })
    })

    try {
      await Promise.all(promises)
    } catch {
      /* ignore */
    } finally {
      setSaving(false)
    }
  }

  const createCycle = () => {
    const now = dayjs()
    const payload = {
      label: now.format('MMMM YYYY'),
      month: now.format('MMMM').toUpperCase(),
    }
    billingCycleApi
      .create(payload)
      .then((res) => {
        const cycle = res.data as BillingCycle
        setCycles((c) => [...c, cycle])
        setCurrentCycle(cycle)
        setLabelInput(cycle.label)
        setMonthInput(cycle.month)
        setProfiles([])
        setExpanded({})
        setCreateOpen(false)
      })
      .catch(() => {
        /* ignore */
      })
  }

  const updateCycle = (payload: { label: string; month: string }) => {
    if (!currentCycle) return
    billingCycleApi
      .update(currentCycle.id, payload)
      .then((res) => {
        const cycle = res.data as BillingCycle
        setCycles((cs) => cs.map((c) => (c.id === cycle.id ? cycle : c)))
        setCurrentCycle(cycle)
        setLabelInput(cycle.label)
        setMonthInput(cycle.month)
      })
      .catch(() => {
        alert('A billing cycle with that label or month already exists.')
        setLabelInput(currentCycle.label)
        setMonthInput(currentCycle.month)
      })
  }

  const handleLabelChange = (val: string) => {
    setLabelInput(val)
    if (!currentCycle) return
    if (cycles.some((c) => c.id !== currentCycle.id && c.label === val)) return
    updateCycle({ label: val, month: monthInput })
  }

  const handleMonthChange = (val: string) => {
    setMonthInput(val)
    if (!currentCycle) return
    if (cycles.some((c) => c.id !== currentCycle.id && c.month === val)) return
    updateCycle({ label: labelInput, month: val })
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
      <div className="flex justify-between items-center">
        <h1 className="page-title">Billing Cycle Planner</h1>
        <Modal
          title="Create new billing cycle?"
          triggerLabel="New Billing Cycle"
          triggerClassName=""
          onOpen={() => setCreateOpen(true)}
        >
          {cycles.some((c) => c.month === dayjs().format('MMMM').toUpperCase()) && (
            <p className="text-destructive text-sm">
              Warning: you already have a cycle for this month.
            </p>
          )}
          <div className="flex justify-end gap-2 mt-4">
            <DialogClose asChild>
              <Button variant="secondary" onClick={() => setCreateOpen(false)}>
                Cancel
              </Button>
            </DialogClose>
            <DialogClose asChild>
              <Button onClick={createCycle}>Confirm</Button>
            </DialogClose>
          </div>
        </Modal>
      </div>
      {currentCycle && (
        <div className="flex flex-col gap-2">
          <p className="text-sm text-foreground">Viewing cycle: {currentCycle.label}</p>
          <div className="flex gap-2 items-end">
            <Input
              className="w-40"
              value={labelInput}
              onChange={(e) => handleLabelChange(e.target.value)}
              placeholder="Label"
            />
            <select
              className="border rounded-md px-2 h-9 text-foreground"
              value={monthInput}
              onChange={(e) => handleMonthChange(e.target.value)}
            >
              {Array.from({ length: 12 }).map((_, i) => {
                const m = dayjs().month(i).format('MMMM').toUpperCase()
                return (
                  <option key={m} value={m}>
                    {m}
                  </option>
                )
              })}
            </select>
          </div>
        </div>
      )}
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
          onClick={() => currentCycle && billingCycleApi.complete(currentCycle.id)}
          disabled={!currentCycle || !!currentCycle.completedDate}
        >
          Complete Bill Payments
        </Button>
      </div>
    </div>
  )
}
