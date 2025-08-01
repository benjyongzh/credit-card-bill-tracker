import { useEffect, useState } from 'react'
import { billPaymentApi } from '@/lib/api'
import type { BillPayment } from '@/lib/dataSchema'

export function useBillPayments() {
  const [payments, setPayments] = useState<BillPayment[]>([])
  const [deletedIds, setDeletedIds] = useState<string[]>([])

  useEffect(() => {
    billPaymentApi
      .getAll()
      .then((res) => setPayments(res.data as BillPayment[]))
      .catch(() => {
        /* ignore */
      })
  }, [])

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

  return { payments, deletedIds, addPayment, updatePayment, removePayment }
}
