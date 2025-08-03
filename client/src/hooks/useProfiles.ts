import { useEffect, useMemo, useState, useCallback } from 'react'
import { spendingProfileApi } from '@/lib/api'
import type { SpendingProfile } from '@/lib/dataSchema'
import { useFetch } from '@/hooks/useFetch'

export interface ExpenseRow {
  id: string
  amount: number
  description: string
  account: string
  profileId: string
  serverId?: string
  dirty?: boolean
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

  const fetchProfiles = useCallback(
    () =>
      spendingProfileApi.getAll().then((res) =>
        (res.data as SpendingProfile[]).map((p) => ({
          id: p.id,
          name: p.name,
          bankAccounts: p.bankAccounts,
          subRows: [],
        })),
      ),
    [],
  )

  const { data, loading, error, reload } = useFetch<ProfileRow[]>(fetchProfiles, {
    errorMessage: 'Failed to load spending profiles',
  })

  useEffect(() => {
    if (data) setProfiles(data)
  }, [data])

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
                  dirty: true,
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
          ? {
              ...p,
              subRows: p.subRows.map((s) =>
                s === expense ? { ...s, ...changes, dirty: true } : s,
              ),
            }
          : p,
      ),
    )
  }

  const allAccounts = useMemo(() => {
    const set = new Set<string>()
    profiles.forEach((p) => p.bankAccounts.forEach((a) => set.add(a)))
    return Array.from(set)
  }, [profiles])

  return {
    profiles,
    setProfiles,
    expanded,
    setExpanded,
    addExpense,
    updateExpense,
    allAccounts,
    loading,
    error,
    reload,
  }
}
