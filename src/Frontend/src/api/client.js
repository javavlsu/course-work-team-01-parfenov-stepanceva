import axios from 'axios'
import { toast } from 'sonner'
import { translateError } from '../i18n'

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

    // Глобальные toast'ы для статусов, которые редко осмысленно обрабатывать в месте вызова.
    // Конкретные ошибки (404, validation) показывают сами хуки/страницы через translateError.
    if (status === 401 && isAuthEndpoint) {
      // refresh/checkAuth вернул 401 — редирект уже обработан выше, toast не нужен
    } else if (status === 403) {
      toast.error(translateError(err, 'errors.forbidden'))
    } else if (status === 409) {
      toast.error(translateError(err, 'errors.conflict'))
    } else if (status === 410) {
      toast.error(translateError(err, 'errors.gone'))
    } else if (status === 500) {
      toast.error(translateError(err, 'errors.SY0001'))
    }

    return Promise.reject(err)
  }
)
