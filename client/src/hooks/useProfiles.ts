import { useEffect, useMemo, useState } from 'react'
import { spendingProfileApi } from '@/lib/api'
import type { SpendingProfile } from '@/lib/dataSchema'

export interface ExpenseRow {
  id: string
  amount: number
  description: string
  account: string
  profileId: string
}

export interface ProfileRow {
  id: string
  name: string
  bankAccounts: string[]
  subRows: ExpenseRow[]
}

export function useProfiles() {
  const [profiles, setProfiles] = useState<ProfileRow[]>([])
  const [expanded, setExpanded] = useState<Record<string, boolean>>({})

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
  }, [])

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

  const updateExpense = (expense: ExpenseRow, changes: Partial<ExpenseRow>) => {
    setProfiles((prev) =>
      prev.map((p) =>
        p.subRows.includes(expense)
          ? { ...p, subRows: p.subRows.map((s) => (s === expense ? { ...s, ...changes } : s)) }
          : p,
      ),
    )
  }

  const allAccounts = useMemo(() => {
    const set = new Set<string>()
    profiles.forEach((p) => p.bankAccounts.forEach((a) => set.add(a)))
    return Array.from(set)
  }, [profiles])

  return { profiles, expanded, setExpanded, addExpense, updateExpense, allAccounts }
}
