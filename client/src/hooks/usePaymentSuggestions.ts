import { useEffect, useState } from 'react'
import api from '@/lib/api'

export interface PaymentSuggestion {
  cardName: string
  amount: number
}

export function usePaymentSuggestions(enabled = true) {
  const [suggestions, setSuggestions] = useState<PaymentSuggestion[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!enabled) return
    setLoading(true)
    api
      .get('/bills/optimizer')
      .then((res) => setSuggestions(res.data))
      .catch(() => setError('Failed to load suggestions'))
      .finally(() => setLoading(false))
  }, [enabled])

  return { suggestions, loading, error }
}
