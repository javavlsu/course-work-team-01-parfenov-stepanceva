import { useEffect } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { motion } from 'framer-motion'
import { AuthLayout } from '../components/auth/AuthLayout'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { useLogin } from '../hooks/useAuth'

const schema = z.object({
  email: z.string().email('Неверный email'),
  password: z.string().min(8, 'Минимум 8 символов').max(100, 'Максимум 100 символов'),
})

export default function LoginPage() {
  const nav = useNavigate()
  const loc = useLocation()
  const login = useLogin()
  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(schema) })

  useEffect(() => {
    if (login.isSuccess) {
      const from = loc.state?.from || '/dashboard'
      nav(from, { replace: true })
    }
  }, [login.isSuccess, loc.state, nav])

  const onSubmit = (data) => login.mutate(data)

  return (
    <AuthLayout title="Вход" subtitle="— Sign in">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {[
          { i: 0, el: <Input key="email" type="email" label="Email" error={errors.email?.message} {...register('email')} /> },
          { i: 1, el: <Input key="pw" type="password" label="Пароль" error={errors.password?.message} {...register('password')} /> },
        ].map((f) => (
          <motion.div key={f.i} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 + f.i * 0.05, duration: 0.3 }}>
            {f.el}
          </motion.div>
        ))}
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.35, duration: 0.3 }} className="pt-2">
          <Button type="submit" size="lg" fullWidth loading={login.isPending}>Войти</Button>
        </motion.div>
      </form>
      <div className="mt-8 text-sm text-gray-600">
        Нет аккаунта?{' '}
        <Link to="/register" className="text-ink font-medium underline-offset-4 hover:underline">Зарегистрироваться →</Link>
      </div>
    </AuthLayout>
  )
}
