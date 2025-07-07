import { useEffect, useState } from 'react'
import api from '@/lib/api'

export function useEntityList<T extends { id: string | number }>(endpoint: string) {
  const [items, setItems] = useState<T[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const load = () => {
    setLoading(true)
    api.get<T[]>(endpoint)
      .then(res => setItems(res.data))
      .catch(() => setError('Failed to load data'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [endpoint])

  const create = async (payload: Partial<T>) => {
    const res = await api.post<T>(endpoint, payload)
    setItems(items => [...items, res.data])
  }

  const update = async (id: string | number, payload: Partial<T>) => {
    const res = await api.put<T>(`${endpoint}/${id}`, payload)
    setItems(items => items.map(it => (it.id === id ? res.data : it)))
  }

  const remove = async (id: string | number) => {
    await api.delete(`${endpoint}/${id}`)
    setItems(items => items.filter(it => it.id !== id))
  }

  return { items, loading, error, create, update, remove, reload: load }
}
