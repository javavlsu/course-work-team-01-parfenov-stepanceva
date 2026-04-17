import { lazy, Suspense, useEffect } from 'react'
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'
import { AnimatePresence } from 'framer-motion'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'sonner'
import { AuthGuard, GuestGuard } from './components/auth/AuthGuard'
import { authApi } from './api/auth'
import { useAuthStore } from './store/authStore'

const LandingPage = lazy(() => import('./pages/LandingPage'))
const LoginPage = lazy(() => import('./pages/LoginPage'))
const RegisterPage = lazy(() => import('./pages/RegisterPage'))
const JoinPage = lazy(() => import('./pages/JoinPage'))
const DashboardPage = lazy(() => import('./pages/DashboardPage'))
const GroupPage = lazy(() => import('./pages/GroupPage'))
const BoardPage = lazy(() => import('./pages/BoardPage'))
const ProfilePage = lazy(() => import('./pages/ProfilePage'))

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, refetchOnWindowFocus: false } },
})

function Loader() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-paper">
      <div className="w-10 h-10 rounded-full border-2 border-gray-200 border-t-ink animate-spin" />
    </div>
  )
}

function SessionBootstrap({ children }) {
  const setUser = useAuthStore((s) => s.setUser)
  const markChecked = useAuthStore((s) => s.markChecked)
  const isChecked = useAuthStore((s) => s.isChecked)

  useEffect(() => {
    let cancelled = false
    authApi.check()
      .then((u) => { if (!cancelled) setUser(u) })
      .catch(() => { if (!cancelled) markChecked() })
    return () => { cancelled = true }
  }, [setUser, markChecked])

  if (!isChecked) return <Loader />
  return children
}

function AnimatedRoutes() {
  const location = useLocation()
  return (
    <AnimatePresence mode="wait">
      <Suspense fallback={<Loader />}>
        <Routes location={location} key={location.pathname}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<GuestGuard><LoginPage /></GuestGuard>} />
          <Route path="/register" element={<GuestGuard><RegisterPage /></GuestGuard>} />
          <Route path="/join/:token" element={<JoinPage />} />
          <Route path="/dashboard" element={<AuthGuard><DashboardPage /></AuthGuard>} />
          <Route path="/groups/:groupId" element={<AuthGuard><GroupPage /></AuthGuard>} />
          <Route path="/boards/:boardId" element={<AuthGuard><BoardPage /></AuthGuard>} />
          <Route path="/profile" element={<AuthGuard><ProfilePage /></AuthGuard>} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Suspense>
    </AnimatePresence>
  )
}

function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-paper">
      <div className="text-center">
        <div className="display-serif text-[10rem] leading-none">404</div>
        <p className="mt-4 text-gray-600">Страница не найдена</p>
        <a href="/" className="mt-6 inline-block underline underline-offset-4">На главную</a>
      </div>
    </div>
  )
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <SessionBootstrap>
          <AnimatedRoutes />
        </SessionBootstrap>
        <Toaster
          position="bottom-right"
          toastOptions={{
            style: { background: '#F5F5F3', color: '#0A0A0A', border: '1px solid #EBEBEA' },
            className: 'mono',
          }}
        />
      </BrowserRouter>
    </QueryClientProvider>
  )
}
