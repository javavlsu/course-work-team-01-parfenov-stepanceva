import { useEffect, useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { motion } from 'framer-motion'
import { AuthLayout } from '../components/auth/AuthLayout'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { useRegister } from '../hooks/useAuth'
import { cn } from '../utils/cn'

const schema = z.object({
  name: z.string().min(3, 'Минимум 3 символа').max(20, 'Максимум 20 символов').regex(/^[A-Za-zА-Яа-яЁё0-9_]+$/, 'Буквы, цифры, _'),
  email: z.string().email('Неверный email'),
  password: z.string().min(8, 'Минимум 8 символов').max(100, 'Максимум 100 символов'),
})

function scorePassword(p = '') {
  let s = 0
  if (p.length >= 8) s++
  if (/\d/.test(p) || /[^A-Za-z0-9]/.test(p)) s++
  if (/[A-Z]/.test(p)) s++
  if (/[A-Z]/.test(p) && /\d/.test(p) && /[^A-Za-z0-9]/.test(p) && p.length >= 12) s++
  return Math.min(4, s)
}

const strengthMeta = [
  { w: 0, color: 'bg-gray-200', label: '' },
  { w: 25, color: 'bg-priority-high', label: 'Слабый' },
  { w: 50, color: 'bg-priority-medium', label: 'Средний' },
  { w: 75, color: 'bg-status-progress', label: 'Хороший' },
  { w: 100, color: 'bg-status-done', label: 'Сильный' },
]

export default function RegisterPage() {
  const nav = useNavigate()
  const loc = useLocation()
  const registerMutation = useRegister()
  const [pw, setPw] = useState('')
  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(schema), mode: 'onBlur' })

  const meta = strengthMeta[scorePassword(pw)]

  useEffect(() => {
    if (registerMutation.isSuccess) {
      const from = loc.state?.from || '/dashboard'
      nav(from, { replace: true })
    }
  }, [registerMutation.isSuccess, loc.state, nav])

  const onSubmit = (data) => registerMutation.mutate(data)

  return (
    <AuthLayout title="Регистрация" subtitle="— Create account">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {[
          <Input key="u" label="Имя пользователя" error={errors.name?.message} {...register('name')} />,
          <Input key="e" type="email" label="Email" error={errors.email?.message} {...register('email')} />,
        ].map((el, i) => (
          <motion.div key={i} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 + i * 0.05, duration: 0.3 }}>
            {el}
          </motion.div>
        ))}
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3, duration: 0.3 }}>
          <Input
            type="password"
            label="Пароль"
            error={errors.password?.message}
            {...register('password')}
            onChange={(e) => { register('password').onChange(e); setPw(e.target.value) }}
          />
          <div className="mt-2 h-[3px] w-full bg-gray-100 rounded overflow-hidden">
            <div className={cn('h-full transition-all duration-[300ms]', meta.color)} style={{ width: `${meta.w}%` }} />
          </div>
          {meta.label && <div className="mt-1 text-xs text-gray-600">{meta.label}</div>}
        </motion.div>
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4, duration: 0.3 }} className="pt-2">
          <Button type="submit" size="lg" fullWidth loading={registerMutation.isPending}>Зарегистрироваться</Button>
        </motion.div>
      </form>
      <div className="mt-8 text-sm text-gray-600">
        Уже есть аккаунт?{' '}
        <Link to="/login" className="text-ink font-medium underline-offset-4 hover:underline">Войти →</Link>
      </div>
    </AuthLayout>
  )
}
