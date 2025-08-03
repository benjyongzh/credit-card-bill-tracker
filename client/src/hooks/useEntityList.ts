import { useEffect, useState, useCallback } from 'react'
import api from '@/lib/api'
import { useFetch } from '@/hooks/useFetch'

export function useEntityList<T extends { id: string | number }>(endpoint: string) {
  const [items, setItems] = useState<T[]>([])

  const fetchItems = useCallback(
    () => api.get<T[]>(endpoint).then((res) => res.data),
    [endpoint],
  )

  const { data, loading, error, reload } = useFetch<T[]>(fetchItems, {
    errorMessage: 'Failed to load data',
  })

  useEffect(() => {
    if (data) setItems(data)
  }, [data])

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

  return { items, loading, error, create, update, remove, reload }
}
