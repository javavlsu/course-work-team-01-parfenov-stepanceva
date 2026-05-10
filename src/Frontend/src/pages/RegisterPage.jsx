import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { motion } from 'framer-motion'
import { AuthLayout } from '../components/auth/AuthLayout'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { useRegister } from '../hooks/useAuth'
import { useTranslation } from '../i18n'
import { cn } from '../utils/cn'

function scorePassword(p = '') {
  let s = 0
  if (p.length >= 8) s++
  if (/\d/.test(p) || /[^A-Za-z0-9]/.test(p)) s++
  if (/[A-Z]/.test(p)) s++
  if (/[A-Z]/.test(p) && /\d/.test(p) && /[^A-Za-z0-9]/.test(p) && p.length >= 12) s++
  return Math.min(4, s)
}

export default function RegisterPage() {
  const nav = useNavigate()
  const loc = useLocation()
  const registerMutation = useRegister()
  const [pw, setPw] = useState('')
  const { t } = useTranslation()

  const schema = useMemo(() => z.object({
    name: z.string().min(3, t('auth.nameTooShort')).max(20).regex(/^[A-Za-zА-Яа-яЁё0-9_]+$/, t('auth.nameLabel')),
    email: z.string().email(t('auth.invalidEmail')),
    password: z.string().min(8, t('auth.passwordTooShort')).max(100),
  }), [t])

  const strengthMeta = [
    { w: 0, color: 'bg-gray-200', label: '' },
    { w: 25, color: 'bg-priority-high', label: t('profile.strengthWeak') },
    { w: 50, color: 'bg-priority-medium', label: t('profile.strengthOk') },
    { w: 75, color: 'bg-status-progress', label: t('profile.strengthGood') },
    { w: 100, color: 'bg-status-done', label: t('profile.strengthStrong') },
  ]

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
    <AuthLayout title={t('auth.registerTitle')} subtitle="— Create account">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {[
          <Input key="u" label={t('auth.nameLabel')} error={errors.name?.message} {...register('name')} />,
          <Input key="e" type="email" label={t('auth.emailLabel')} error={errors.email?.message} {...register('email')} />,
        ].map((el, i) => (
          <motion.div key={i} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 + i * 0.05, duration: 0.3 }}>
            {el}
          </motion.div>
        ))}
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3, duration: 0.3 }}>
          <Input
            type="password"
            label={t('auth.passwordLabel')}
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
          <Button type="submit" size="lg" fullWidth loading={registerMutation.isPending}>{t('auth.submitRegister')}</Button>
        </motion.div>
      </form>
      <div className="mt-8 text-sm text-gray-600">
        {t('auth.haveAccount')}{' '}
        <Link to="/login" className="text-ink font-medium underline-offset-4 hover:underline">{t('auth.loginLink')} →</Link>
      </div>
    </AuthLayout>
  )
}
