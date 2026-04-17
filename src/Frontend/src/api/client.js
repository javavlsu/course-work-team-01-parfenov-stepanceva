import axios from 'axios'
import { toast } from 'sonner'

export const API_BASE = import.meta.env.VITE_API_BASE ?? '/kanban/api'

export const client = axios.create({
  baseURL: API_BASE,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

let refreshing = null

client.interceptors.response.use(
  (res) => res,
  async (err) => {
    const status = err.response?.status
    const cfg = err.config || {}
    const url = cfg.url || ''

    // /auth/refresh должен оставаться здесь, чтобы не зациклиться при недействительном refresh-токене.
    // /auth/checkAuth убран — чтобы при истёкшем access-токене перехватчик мог вызвать refresh.
    const isAuthEndpoint = url.includes('/auth/login') || url.includes('/auth/registration') || url.includes('/auth/refresh')

    if (status === 401 && !cfg._retry && !isAuthEndpoint) {
      cfg._retry = true
      try {
        refreshing = refreshing || client.post('/auth/refresh')
        await refreshing
        refreshing = null
        return client(cfg)
      } catch {
        refreshing = null
        if (!window.location.pathname.startsWith('/login') && !window.location.pathname.startsWith('/register') && window.location.pathname !== '/') {
          window.location.href = '/login'
        }
        return Promise.reject(err)
      }
    }

    const msg = err.response?.data?.message
    if (status === 401 && isAuthEndpoint) {
      // refresh/checkAuth вернул 401 — редирект обработан в interceptor выше, toast не нужен
    } else if (status === 403) toast.error(msg || 'Недостаточно прав')
    else if (status === 409) toast.error(msg || 'Конфликт данных')
    else if (status === 410) toast.error(msg || 'Ссылка или приглашение истекло')
    else if (status === 500) toast.error(msg || 'Ошибка сервера')

    return Promise.reject(err)
  }
)
