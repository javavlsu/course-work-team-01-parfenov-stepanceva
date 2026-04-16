import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'

export function AuthGuard({ children }) {
  const isAuth = useAuthStore((s) => s.isAuthenticated)
  const loc = useLocation()
  if (!isAuth) return <Navigate to="/login" state={{ from: loc.pathname }} replace />
  return children
}

export function GuestGuard({ children }) {
  const isAuth = useAuthStore((s) => s.isAuthenticated)
  if (isAuth) return <Navigate to="/dashboard" replace />
  return children
}
