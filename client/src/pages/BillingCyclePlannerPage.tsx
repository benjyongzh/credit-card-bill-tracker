import { useEffect, useState } from 'react'
import { formatNumber } from 'accounting-js'
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
import { TrashIcon } from 'lucide-react'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import EditableTable from '@/components/EditableTable'

interface ExpenseRow {
  id: string
  serverId?: string
  amount: number
  description: string
  account: string
  profileId: string
  dirty?: boolean
}

interface ProfileRow {
  id: string
  name: string
  bankAccounts: string[]
  subRows: ExpenseRow[]
}

interface PaymentRow {
  id: string
  serverId?: string
  fromAccount: string
  toCard: string
  amount: number
  dirty?: boolean
}

export default function BillingCyclePlannerPage() {
  const [allProfiles, setAllProfiles] = useState<SpendingProfile[]>([])
  const [profiles, setProfiles] = useState<ProfileRow[]>([])
  const [saving, setSaving] = useState(false)
  const [deletedExpenses, setDeletedExpenses] = useState<string[]>([])
  const [billPayments, setBillPayments] = useState<PaymentRow[]>([])
  const [deletedPayments, setDeletedPayments] = useState<string[]>([])
  const [suggestions, setSuggestions] = useState<any[]>([])
  const [currentCycle, setCurrentCycle] = useState<BillingCycle | null>(null)
  const [cycles, setCycles] = useState<BillingCycle[]>([])
  const [accounts, setAccounts] = useState<BankAccount[]>([])
  const [cards, setCards] = useState<Card[]>([])
  const [createOpen, setCreateOpen] = useState(false)
  const [profileSelect, setProfileSelect] = useState('')
  const [profileToDelete, setProfileToDelete] = useState<ProfileRow | null>(null)
  const [labelInput, setLabelInput] = useState('')
  const [monthInput, setMonthInput] = useState(dayjs().format('MMMM').toUpperCase())

  // initial load
  useEffect(() => {
    spendingProfileApi
      .getAll()
      .then((res) => {
        setAllProfiles(res.data as SpendingProfile[])
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

    const creates: { promise: Promise<any>; profileId: string; rowId: string }[] = []
    const updates: any[] = []
    const deletes = [...deletedExpenses]

    const bpCreates: { promise: Promise<any>; rowId: string }[] = []
    const bpUpdates: any[] = []
    const bpDeletes = [...deletedPayments]

    profiles.forEach((p) => {
      p.subRows.forEach((e) => {
        const accId = accountMap[e.account]
        if (!accId || !cards[0]) return
        const payload = {
          id: e.serverId,
          creditCardId: cards[0].id,
          date: dayjs().format('YYYY-MM-DD'),
          amount: e.amount,
          description: e.description,
          bankAccountIds: [accId],
          billingCycleId: currentCycle.id,
        }
        if (!e.serverId) {
          creates.push({
            profileId: p.id,
            rowId: e.id,
            promise: expenseApi.create(payload, p.id),
          })
        } else if (e.dirty) {
          updates.push(payload)
        }
      })
    })

    billPayments.forEach((bp) => {
      if (!cards[0]) return
      const payload = {
        id: bp.serverId,
        fromAccountId: bp.fromAccount,
        toCardId: bp.toCard,
        toAccountId: null,
        date: dayjs().format('YYYY-MM-DD'),
        amount: bp.amount,
        billingCycleId: currentCycle.id,
      }
      if (!bp.serverId) {
        bpCreates.push({ rowId: bp.id, promise: billPaymentApi.create(payload) })
      } else if (bp.dirty) {
        bpUpdates.push(payload)
      }
    })

    try {
      if (deletes.length) {
        await Promise.all(deletes.map((id) => expenseApi.remove(id)))
        setDeletedExpenses([])
      }
      if (updates.length) {
        await expenseApi.updateMany(updates)
      }
      if (bpDeletes.length) {
        await Promise.all(bpDeletes.map((id) => billPaymentApi.remove(id)))
        setDeletedPayments([])
      }
      if (bpUpdates.length) {
        await billPaymentApi.updateMany(bpUpdates)
      }
      await Promise.all(
        creates.map((c) =>
          c.promise.then((res) => ({ ...c, id: res.data.id as string })),
        ),
      ).then((results) => {
        setProfiles((prev) =>
          prev.map((p) => ({
            ...p,
            subRows: p.subRows.map((r) => {
              const match = results.find(
                (res) => res.profileId === p.id && res.rowId === r.id,
              )
              if (match) {
                return { ...r, serverId: match.id, dirty: false }
              }
              if (r.serverId && updates.some((u) => u.id === r.serverId)) {
                return { ...r, dirty: false }
              }
              return r
            }),
          }))
        )
      })
      await Promise.all(
        bpCreates.map((c) =>
          c.promise.then((res) => ({ ...c, id: res.data.id as string })),
        ),
      ).then((results) => {
        setBillPayments((prev) =>
          prev.map((r) => {
            const match = results.find((res) => res.rowId === r.id)
            if (match) {
              return { ...r, serverId: match.id, dirty: false }
            }
            if (r.serverId && bpUpdates.some((u) => u.id === r.serverId)) {
              return { ...r, dirty: false }
            }
            return r
          }),
        )
      })
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
        setBillPayments([])
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

  const addProfile = (profileId: string) => {
    const profile = allProfiles.find((p) => p.id === profileId)
    if (!profile) return
    setProfiles((prev) => [
      ...prev,
      {
        id: profile.id,
        name: profile.name,
        bankAccounts: profile.bankAccounts,
        subRows: [],
      },
    ])
  }

  const removeProfile = (profile: ProfileRow) => {
    setProfiles((prev) => prev.filter((p) => p.id !== profile.id))
    const ids = profile.subRows
      .filter((e) => e.serverId)
      .map((e) => e.serverId!)
    if (ids.length) {
      setDeletedExpenses((d) => [...d, ...ids])
    }
  }

  const deleteProfile = (profile: ProfileRow) => {
    if (profile.subRows.length) {
      setProfileToDelete(profile)
    } else {
      removeProfile(profile)
    }
  }

  const addExpense = (profileId: string, afterId?: string) => {
    setProfiles((prev) =>
      prev.map((p) => {
        if (p.id !== profileId) return p
        const newRow: ExpenseRow = {
          id: Math.random().toString(),
          dirty: true,
          amount: 0,
          description: '',
          account: p.bankAccounts[0] || '',
          profileId,
        }
        if (!afterId) {
          return { ...p, subRows: [...p.subRows, newRow] }
        }
        const idx = p.subRows.findIndex((r) => r.id === afterId)
        if (idx === -1) {
          return { ...p, subRows: [...p.subRows, newRow] }
        }
        return {
          ...p,
          subRows: [
            ...p.subRows.slice(0, idx + 1),
            newRow,
            ...p.subRows.slice(idx + 1),
          ],
        }
      }),
    )
  }

  const deleteExpense = (expense: ExpenseRow) => {
    setProfiles((prev) =>
      prev.map((p) =>
        p.id === expense.profileId
          ? {
              ...p,
              subRows: p.subRows.filter((r) => r !== expense),
            }
          : p,
      ),
    )
    if (expense.serverId) {
      setDeletedExpenses((d) => [...d, expense.serverId!])
    }
  }

  const addBillPayment = (afterId?: string) => {
    const newRow: PaymentRow = {
      id: Math.random().toString(),
      fromAccount: accounts[0]?.id || '',
      toCard: cards[0]?.id || '',
      amount: 0,
      dirty: true,
    }
    setBillPayments((prev) => {
      if (!afterId) return [...prev, newRow]
      const idx = prev.findIndex((p) => p.id === afterId)
      if (idx === -1) return [...prev, newRow]
      return [...prev.slice(0, idx + 1), newRow, ...prev.slice(idx + 1)]
    })
  }

  const deleteBillPayment = (payment: PaymentRow) => {
    setBillPayments((prev) => prev.filter((p) => p !== payment))
    if (payment.serverId) {
      setDeletedPayments((d) => [...d, payment.serverId!])
    }
  }
  const allAccounts = Array.from(
    new Set(profiles.flatMap((p) => p.bankAccounts)),
  )

  const paymentColumns = [
    {
      header: 'From Account',
      render: (
        bp: PaymentRow,
        update: (p: Partial<PaymentRow>) => void,
        onKeyDown: (e: React.KeyboardEvent) => void,
      ) => (
        <select
          className="border px-1 text-foreground"
          value={bp.fromAccount}
          onChange={(e) => update({ fromAccount: e.target.value })}
          onKeyDown={onKeyDown}
        >
          {accounts.map((a) => (
            <option key={a.id} value={a.id}>
              {a.name}
            </option>
          ))}
        </select>
      ),
    },
    {
      header: 'Amount',
      render: (
        bp: PaymentRow,
        update: (p: Partial<PaymentRow>) => void,
        onKeyDown: (e: React.KeyboardEvent) => void,
      ) => (
        <input
          type="number"
          className="w-24 border px-1"
          value={bp.amount}
          onChange={(e) => update({ amount: parseFloat(e.target.value) })}
          onKeyDown={onKeyDown}
        />
      ),
    },
  ]

  return (
    <div className="flex flex-col gap-6 max-w-6xl mx-auto">
      <div className="flex justify-between items-center">
        <h1 className="page-title">Billing Cycle Planner</h1>
        <Modal
          title="Create new billing cycle?"
          triggerLabel="New Billing Cycle"
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
      <div className="flex justify-end">
        <Modal
          title="Add Spending Category"
          triggerLabel="Add Category"
          triggerClassName="mb-2"
          onOpen={() => setProfileSelect('')}
        >
          <select
            className="border w-full p-2 mb-4 text-foreground"
            value={profileSelect}
            onChange={(e) => setProfileSelect(e.target.value)}
          >
            <option value="">Select...</option>
            {allProfiles
              .filter((p) => !profiles.some((pr) => pr.id === p.id))
              .map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))}
          </select>
          <div className="flex justify-end gap-2">
            <DialogClose asChild>
              <Button
                onClick={() => {
                  if (profileSelect) addProfile(profileSelect)
                }}
                disabled={!profileSelect}
              >
                Add
              </Button>
            </DialogClose>
          </div>
        </Modal>
      </div>
      {profiles.map((profile) => {
        const expenseColumns = [
          {
            header: 'Account',
            render: (
              row: ExpenseRow,
              update: (p: Partial<ExpenseRow>) => void,
              onKeyDown: (e: React.KeyboardEvent) => void,
            ) => (
              <select
                className="border px-1 text-foreground"
                value={row.account}
                onChange={(e) => update({ account: e.target.value })}
                onKeyDown={onKeyDown}
              >
                {profile.bankAccounts.map((a) => (
                  <option key={a} value={a}>
                    {a}
                  </option>
                ))}
              </select>
            ),
          },
          {
            header: 'Amount',
            render: (
              row: ExpenseRow,
              update: (p: Partial<ExpenseRow>) => void,
              onKeyDown: (e: React.KeyboardEvent) => void,
            ) => (
              <input
                type="number"
                className="w-24 border px-1"
                value={row.amount}
                onChange={(e) => update({ amount: parseFloat(e.target.value) })}
                onKeyDown={onKeyDown}
              />
            ),
          },
          {
            header: 'Description',
            render: (
              row: ExpenseRow,
              update: (p: Partial<ExpenseRow>) => void,
              onKeyDown: (e: React.KeyboardEvent) => void,
            ) => (
              <input
                type="text"
                className="w-full border px-1"
                value={row.description}
                onChange={(e) => update({ description: e.target.value })}
                onKeyDown={onKeyDown}
              />
            ),
          },
        ]
        return (
          <div key={profile.id} className="mt-4">
            <div className="flex justify-between items-center mb-2">
              <span className="font-semibold text-foreground">{profile.name}</span>
              <Button
                size="icon"
                variant="ghost"
                className="text-destructive"
                onClick={() => deleteProfile(profile)}
              >
                <TrashIcon className="size-4" />
              </Button>
            </div>
            <EditableTable
              rows={profile.subRows}
              setRows={(rows) =>
                setProfiles((prev) =>
                  prev.map((p) =>
                    p.id === profile.id ? { ...p, subRows: rows } : p,
                  ),
                )
              }
              columns={expenseColumns}
              makeRow={() => ({
                id: Math.random().toString(),
                profileId: profile.id,
                amount: 0,
                description: '',
                account: profile.bankAccounts[0] || '',
                dirty: true,
              })}
              addLabel="Add Expense"
              onDeleteRow={(r) =>
                r.serverId && setDeletedExpenses((d) => [...d, r.serverId!])
              }
            />
          </div>
        )
      })}
      <div className="flex justify-end gap-2 mt-4">
        <Button onClick={handleSave} disabled={saving}>
          {saving ? 'Saving...' : 'Save'}
        </Button>
      </div>
      <h2 className="font-bold mt-6 mb-2 text-foreground">Bill Payments</h2>
      <EditableTable
        rows={billPayments}
        setRows={setBillPayments}
        columns={paymentColumns}
        makeRow={() => ({
          id: Math.random().toString(),
          fromAccount: accounts[0]?.id || '',
          toCard: cards[0]?.id || '',
          amount: 0,
          dirty: true,
        })}
        addLabel="Add Bill Payment"
        onDeleteRow={(r) =>
          r.serverId && setDeletedPayments((d) => [...d, r.serverId!])
        }
      />
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
      <Modal
        title="Delete category?"
        open={!!profileToDelete}
        onOpenChange={(o) => !o && setProfileToDelete(null)}
      >
        <p className="mb-4 text-foreground">
          This will also remove all expenses under {profileToDelete?.name}.
        </p>
        <div className="flex justify-end gap-2">
          <Button
            variant="destructive"
            onClick={() => {
              if (profileToDelete) removeProfile(profileToDelete)
              setProfileToDelete(null)
            }}
          >
            Confirm
          </Button>
        </div>
      </Modal>
    </div>
  )
}
