import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { CheckCircle2, XCircle } from 'lucide-react'
import { Button } from '../components/ui/Button'
import { useAuthStore } from '../store/authStore'
import { useJoinByToken } from '../hooks/useInvitations'

export default function JoinPage() {
  const { token } = useParams()
  const nav = useNavigate()
  const isAuth = useAuthStore((s) => s.isAuthenticated)
  const isChecked = useAuthStore((s) => s.isChecked)
  const join = useJoinByToken()
  const [result, setResult] = useState(null)
  const [errorMsg, setErrorMsg] = useState('')
  const [attempted, setAttempted] = useState(false)

  useEffect(() => {
    if (!isChecked || !isAuth || attempted) return
    setAttempted(true)
    join.mutate(token, {
      onSuccess: (inv) => setResult(inv),
      onError: (err) => {
        const status = err.response?.status
        if (status === 404) setErrorMsg('Приглашение не найдено')
        else if (status === 409) setErrorMsg('Вы уже состоите в этой группе')
        else if (status === 410) setErrorMsg('Срок действия ссылки истёк')
        else setErrorMsg('Не удалось принять приглашение')
      },
    })
  }, [isChecked, isAuth, attempted, token, join])

  const groupName = result?.group?.name
  const groupId = result?.group?.id

  return (
    <div className="min-h-screen flex items-center justify-center px-6 bg-paper">
      <motion.div initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }} className="max-w-lg w-full text-center">
        <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-3">Приглашение</div>
        <h1 className="display-serif text-3xl md:text-5xl leading-none">
          {groupName ? groupName.toUpperCase() : 'ПРИГЛАШЕНИЕ'}
        </h1>
        <p className="mt-4 text-gray-600">Токен: <span className="mono text-sm">{token}</span></p>

        {isChecked && !isAuth && (
          <div className="mt-10 space-y-4">
            <p className="text-base text-gray-600">Для вступления необходимо войти в аккаунт</p>
            <div className="flex items-center justify-center gap-3">
              <Link to="/login" state={{ from: `/join/${token}` }}><Button size="lg">Войти</Button></Link>
              <Link to="/register" state={{ from: `/join/${token}` }}><Button variant="outline" size="lg">Зарегистрироваться</Button></Link>
            </div>
          </div>
        )}

        {isAuth && join.isPending && (
          <div className="mt-12 flex flex-col items-center gap-3">
            <div className="w-10 h-10 rounded-full border-2 border-gray-200 border-t-ink animate-spin" />
            <p className="text-sm text-gray-600">Обработка приглашения…</p>
          </div>
        )}

        {isAuth && result && (
          <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="mt-12 space-y-6">
            <CheckCircle2 className="w-14 h-14 mx-auto text-status-done" />
            <p className="text-lg">Вы вступили в команду «{groupName}».</p>
            <Button size="lg" onClick={() => nav(groupId ? `/groups/${groupId}` : '/dashboard')}>Перейти к группе</Button>
          </motion.div>
        )}

        {isAuth && errorMsg && (
          <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="mt-12 space-y-6">
            <XCircle className="w-14 h-14 mx-auto text-priority-high" />
            <p className="text-lg">{errorMsg}</p>
            <Button size="lg" variant="outline" onClick={() => nav('/dashboard')}>На главную</Button>
          </motion.div>
        )}
      </motion.div>
    </div>
  )
}
