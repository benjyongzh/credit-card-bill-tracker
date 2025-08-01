import { useEffect, useState } from 'react'
import dayjs from 'dayjs'
import {
  billOptimizerApi,
  billingCycleApi,
  creditCardApi,
  bankAccountApi,
  expenseApi,
  spendingProfileApi,
  billPaymentApi,
} from '@/lib/api'
import type { BillingCycle, Card, BankAccount } from '@/lib/dataSchema'
import { Button } from '@/components/ui/button'
import Modal from '@/components/Modal'
import { DialogClose } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import ProfileSection from '@/components/ProfileSection'
import BillPaymentSection from '@/components/BillPaymentSection'
import { useProfiles, type ProfileRow } from '@/hooks/useProfiles'

interface PaymentRow {
  id: string
  serverId?: string
  fromAccount: string
  toCard: string
  amount: number
  dirty?: boolean
}

export default function BillingCyclePlannerPage() {
  const {
    profiles,
    setProfiles,
  } = useProfiles()

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

  useEffect(() => {
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

  const removeProfile = (profile: ProfileRow) => {
    spendingProfileApi.remove(profile.id).catch(() => {
      /* ignore */
    })
    setProfiles((prev) => prev.filter((p) => p.id !== profile.id))
  }

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
      <ProfileSection />
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
      <BillPaymentSection accounts={accounts} cards={cards} />
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
