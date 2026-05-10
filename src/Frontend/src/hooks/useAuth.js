import { useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { authApi } from '../api/auth'
import { useAuthStore } from '../store/authStore'
import { t } from '../i18n'

export const authKey = ['auth', 'me']

export function useSessionCheck() {
  const setUser = useAuthStore((s) => s.setUser)
  const logout = useAuthStore((s) => s.logout)
  const q = useQuery({
    queryKey: authKey,
    queryFn: authApi.check,
    retry: false,
    staleTime: 5 * 60 * 1000,
  })

  useEffect(() => {
    if (q.isSuccess) setUser(q.data)
    else if (q.isError) logout()
  }, [q.isSuccess, q.isError, q.data, setUser, logout])

  return q
}

export function useLogin() {
  const setUser = useAuthStore((s) => s.setUser)
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.login,
    onSuccess: (user) => {
      setUser(user)
      qc.setQueryData(authKey, user)
      toast.success(t('auth.loginSuccess', { name: user.username }))
    },
    onError: (err) => {
      const status = err.response?.status
      if (status === 400 || status === 401) toast.error(t('auth.loginFailed'))
    },
  })
}

export function useRegister() {
  const setUser = useAuthStore((s) => s.setUser)
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.register,
    onSuccess: (user) => {
      setUser(user)
      qc.setQueryData(authKey, user)
      toast.success(t('auth.registerSuccess'))
    },
    onError: (err) => {
      if (err.response?.status === 409) toast.error(t('auth.emailTaken'))
    },
  })
}

export function useLogout() {
  const nav = useNavigate()
  const logoutLocal = useAuthStore((s) => s.logout)
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.logout,
    onSettled: () => {
      logoutLocal()
      qc.clear()
      nav('/login', { replace: true })
    },
  })
}
