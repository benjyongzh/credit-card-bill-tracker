import { useEffect, useState, useCallback } from 'react'
import { billPaymentApi } from '@/lib/api'
import type { BillPayment } from '@/lib/dataSchema'
import { useFetch } from '@/hooks/useFetch'

export function useBillPayments() {
  const [payments, setPayments] = useState<BillPayment[]>([])
  const [deletedIds, setDeletedIds] = useState<string[]>([])

  const fetchPayments = useCallback(
    () => billPaymentApi.getAll().then((res) => res.data as BillPayment[]),
    [],
  )

  const { data, loading, error, reload } = useFetch(fetchPayments, {
    errorMessage: 'Failed to load bill payments',
  })

  useEffect(() => {
    if (data) setPayments(data)
  }, [data])

  const addPayment = (payment: BillPayment) => {
    setPayments((p) => [...p, payment])
  }

  const updatePayment = (id: string, changes: Partial<BillPayment>) => {
    setPayments((p) => p.map((bp) => (bp.id === id ? { ...bp, ...changes } : bp)))
  }

  const removePayment = (id: string) => {
    setPayments((p) => p.filter((bp) => bp.id !== id))
    setDeletedIds((d) => [...d, id])
  }

  return {
    payments,
    deletedIds,
    addPayment,
    updatePayment,
    removePayment,
    loading,
    error,
    reload,
  }
}
