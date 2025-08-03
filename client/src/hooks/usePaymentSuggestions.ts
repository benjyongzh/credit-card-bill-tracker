import { useEffect, useCallback } from 'react'
import api from '@/lib/api'
import { useFetch } from '@/hooks/useFetch'

export interface PaymentSuggestion {
  cardName: string
  amount: number
}

export function usePaymentSuggestions(enabled = true) {
  const fetchSuggestions = useCallback(
    () => api.get('/bills/optimizer').then((res) => res.data),
    [],
  )

  const { data, loading, error, reload } = useFetch<PaymentSuggestion[]>(
    fetchSuggestions,
    {
      immediate: false,
      errorMessage: 'Failed to load suggestions',
    },
  )

  useEffect(() => {
    if (enabled) reload()
  }, [enabled, reload])

  return { suggestions: data ?? [], loading, error }
}
