import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'

function Loader() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-paper">
      <div className="w-10 h-10 rounded-full border-2 border-gray-200 border-t-ink animate-spin" />
    </div>
  )
}

export function AuthGuard({ children }) {
  const isAuth = useAuthStore((s) => s.isAuthenticated)
  const isChecked = useAuthStore((s) => s.isChecked)
  const loc = useLocation()
  if (!isChecked) return <Loader />
  if (!isAuth) return <Navigate to="/login" state={{ from: loc.pathname }} replace />
  return children
}

export function GuestGuard({ children }) {
  const isAuth = useAuthStore((s) => s.isAuthenticated)
  const isChecked = useAuthStore((s) => s.isChecked)
  if (!isChecked) return <Loader />
  if (isAuth) return <Navigate to="/dashboard" replace />
  return children
}
