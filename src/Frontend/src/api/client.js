import axios from 'axios'
import { toast } from 'sonner'

export const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

export const client = axios.create({
  baseURL: API_BASE,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.response.use(
  (res) => res,
  async (err) => {
    const status = err.response?.status
    const cfg = err.config || {}

    if (status === 401 && !cfg._retry && !cfg.url?.includes('/auth/')) {
      cfg._retry = true
      try {
        await client.post('/auth/refresh')
        return client(cfg)
      } catch {
        window.location.href = '/login'
      }
    }
    if (status === 403) toast.error('Недостаточно прав')
    else if (status === 409) toast.error(err.response?.data?.message || 'Конфликт')
    else if (status === 410) toast.error('Ссылка или приглашение истекло')
    else if (status === 500) toast.error('Ошибка сервера')

    return Promise.reject(err)
  }
)
