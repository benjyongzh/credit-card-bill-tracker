import { useState, useCallback, useEffect } from 'react'

interface UseFetchOptions<T> {
  immediate?: boolean
  initialData?: T | null
  errorMessage?: string
}

export function useFetch<T>(
  fetcher: () => Promise<T>,
  { immediate = true, initialData = null, errorMessage = 'Failed to load data' }: UseFetchOptions<T> = {},
) {
  const [data, setData] = useState<T | null>(initialData)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(() => {
    setLoading(true)
    setError(null)
    return fetcher()
      .then((res) => setData(res))
      .catch(() => setError(errorMessage))
      .finally(() => setLoading(false))
  }, [fetcher, errorMessage])

  useEffect(() => {
    if (immediate) {
      load()
    }
  }, [immediate, load])

  return { data, loading, error, reload: load }
}
