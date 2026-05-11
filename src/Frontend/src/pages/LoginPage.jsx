import { useEffect, useMemo } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { motion } from 'framer-motion'
import { AuthLayout } from '../components/auth/AuthLayout'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { useLogin } from '../hooks/useAuth'
import { useTranslation } from '../i18n'

export default function LoginPage() {
  const nav = useNavigate()
  const loc = useLocation()
  const login = useLogin()
  const { t } = useTranslation()

  const schema = useMemo(() => z.object({
    email: z.string().email(t('auth.invalidEmail')),
    password: z.string().min(8, t('auth.passwordTooShort')).max(100),
  }), [t])

  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(schema) })

  useEffect(() => {
    if (login.isSuccess) {
      const from = loc.state?.from || '/dashboard'
      nav(from, { replace: true })
    }
  }, [login.isSuccess, loc.state, nav])

  const onSubmit = (data) => login.mutate(data)

  return (
    <AuthLayout title={t('auth.loginTitle')} subtitle={t('auth.loginSubtitle')}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {[
          { i: 0, el: <Input key="email" type="email" label={t('auth.emailLabel')} error={errors.email?.message} {...register('email')} /> },
          { i: 1, el: <Input key="pw" type="password" label={t('auth.passwordLabel')} error={errors.password?.message} {...register('password')} /> },
        ].map((f) => (
          <motion.div key={f.i} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 + f.i * 0.05, duration: 0.3 }}>
            {f.el}
          </motion.div>
        ))}
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.35, duration: 0.3 }} className="pt-2">
          <Button type="submit" size="lg" fullWidth loading={login.isPending}>{t('auth.submitLogin')}</Button>
        </motion.div>
      </form>
      <div className="mt-8 text-sm text-gray-600">
        {t('auth.noAccount')}{' '}
        <Link to="/register" className="text-ink font-medium underline-offset-4 hover:underline">{t('auth.registerLink')} →</Link>
      </div>
    </AuthLayout>
  )
}
