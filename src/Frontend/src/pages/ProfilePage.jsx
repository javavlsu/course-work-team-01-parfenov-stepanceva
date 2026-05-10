import { useRef, useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { Camera } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { PageTransition } from '../components/layout/PageTransition'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Avatar } from '../components/ui/Avatar'
import { useAuthStore } from '../store/authStore'
import { usersApi } from '../api/resources'
import { translateError, useTranslation } from '../i18n'
import { formatDate } from '../utils/dates'
import { toast } from 'sonner'
import { cn } from '../utils/cn'

function scorePassword(p = '') {
  let s = 0
  if (p.length >= 8) s++
  if (/\d/.test(p) || /[^A-Za-z0-9]/.test(p)) s++
  if (/[A-Z]/.test(p)) s++
  if (p.length >= 12) s++
  return Math.min(4, s)
}

export default function ProfilePage() {
  const currentUser = useAuthStore((s) => s.user)
  const setAuthUser = useAuthStore((s) => s.setUser)
  const fileRef = useRef(null)
  const { t } = useTranslation()

  const strengthMeta = [
    { w: 0, color: 'bg-gray-200', label: '' },
    { w: 25, color: 'bg-priority-high', label: t('profile.strengthWeak') },
    { w: 50, color: 'bg-priority-medium', label: t('profile.strengthOk') },
    { w: 75, color: 'bg-status-progress', label: t('profile.strengthGood') },
    { w: 100, color: 'bg-status-done', label: t('profile.strengthStrong') },
  ]

  const [username, setUsername] = useState(currentUser?.username || '')
  const [avatarPreview, setAvatarPreview] = useState(null)
  const [avatarFile, setAvatarFile] = useState(null)
  const [pw, setPw] = useState({ current: '', next: '' })
  const meta = strengthMeta[scorePassword(pw.next)]

  const updateName = useMutation({
    mutationFn: (name) => usersApi.updateName(name),
    onSuccess: (u) => { setAuthUser(u); toast.success(t('profile.nameSaved')) },
    onError: (err) => {
      const fieldError = err.response?.data?.details?.fields?.name
      toast.error(fieldError || translateError(err, 'profile.nameSaveFailed'))
    },
  })

  const updatePassword = useMutation({
    mutationFn: ({ oldPassword, newPassword }) => usersApi.updatePassword(oldPassword, newPassword),
    onSuccess: () => { setPw({ current: '', next: '' }); toast.success(t('profile.passwordUpdated')) },
    onError: (err) => {
      const status = err.response?.status
      if (status === 400) toast.error(t('profile.passwordBadCurrent'))
      else toast.error(t('profile.passwordFailed'))
    },
  })

  const uploadAvatar = useMutation({
    mutationFn: (file) => usersApi.uploadAvatar(file),
    onSuccess: (u) => {
      setAuthUser(u)
      setAvatarPreview(null); setAvatarFile(null)
      toast.success(t('profile.avatarSaved'))
    },
    onError: (err) => toast.error(translateError(err, 'profile.avatarFailed')),
  })

  const onFile = (file) => {
    if (!file) return
    if (file.size > 5 * 1024 * 1024) return toast.error(t('tasks.fileTooLarge', { max: 5 }))
    setAvatarFile(file)
    const reader = new FileReader()
    reader.onload = () => setAvatarPreview(reader.result)
    reader.readAsDataURL(file)
  }

  const applyAvatar = () => {
    if (avatarFile) uploadAvatar.mutate(avatarFile)
  }

  const saveUsername = () => {
    if (!username.trim()) return toast.error(t('auth.nameTooShort'))
    if (username.trim().length < 3 || username.trim().length > 20) return toast.error(t('auth.nameTooShort'))
    updateName.mutate(username.trim())
  }

  const savePassword = () => {
    if (!pw.current || !pw.next) return toast.error(t('errors.VA0001'))
    if (pw.next.length < 8) return toast.error(t('auth.passwordTooShort'))
    updatePassword.mutate({ oldPassword: pw.current, newPassword: pw.next })
  }

  if (!currentUser) return null

  return (
    <AppShell breadcrumb={[{ label: t('profile.title') }]}>
      <PageTransition className="max-w-3xl mx-auto px-6 md:px-12 py-10">
        <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-2">{t('profile.title')}</div>
        <h1 className="display-serif text-3xl md:text-4xl mb-10">{t('common.settings')}</h1>

        <section className="bg-paper border border-gray-100 rounded-lg p-8 mb-8">
          <div className="flex flex-col sm:flex-row items-start gap-8">
            <div className="relative group">
              <Avatar user={{ ...currentUser, avatar: avatarPreview || currentUser.avatar }} size="2xl" />
              <button
                onClick={() => fileRef.current?.click()}
                aria-label={t('profile.avatarChange')}
                className="absolute inset-0 rounded-full flex items-center justify-center bg-ink/60 text-paper opacity-0 group-hover:opacity-100 transition-opacity"
              >
                <Camera className="w-6 h-6" />
              </button>
              <input ref={fileRef} type="file" accept="image/jpeg,image/png,image/webp" className="hidden" onChange={(e) => onFile(e.target.files?.[0])} />
            </div>
            <div className="flex-1 space-y-2">
              <h2 className="display-serif text-2xl">{currentUser.username}</h2>
              <p className="text-gray-600">{currentUser.email}</p>
              <p className="text-xs text-gray-400">{t('profile.memberSince')}: {formatDate(currentUser.createdAt)}</p>
              {avatarPreview && (
                <div className="flex gap-2 pt-3">
                  <Button size="sm" onClick={applyAvatar} loading={uploadAvatar.isPending}>{t('common.apply')}</Button>
                  <Button size="sm" variant="ghost" onClick={() => { setAvatarPreview(null); setAvatarFile(null) }}>{t('common.cancel')}</Button>
                </div>
              )}
            </div>
          </div>
        </section>

        <section className="bg-paper border border-gray-100 rounded-lg p-8 mb-8">
          <h3 className="display-serif text-xl mb-4">{t('profile.nameLabel')}</h3>
          <div className="flex flex-col sm:flex-row gap-3 items-stretch sm:items-end">
            <div className="flex-1">
              <Input label={t('profile.nameLabel')} value={username} onChange={(e) => setUsername(e.target.value)} />
            </div>
            <Button onClick={saveUsername} loading={updateName.isPending}>{t('common.save')}</Button>
          </div>
        </section>

        <section className="bg-paper border border-gray-100 rounded-lg p-8">
          <h3 className="display-serif text-xl mb-4">{t('profile.passwordTitle')}</h3>
          <div className="grid sm:grid-cols-2 gap-3 mb-3">
            <Input type="password" label={t('profile.currentPassword')} value={pw.current} onChange={(e) => setPw({ ...pw, current: e.target.value })} />
            <div>
              <Input type="password" label={t('profile.newPassword')} value={pw.next} onChange={(e) => setPw({ ...pw, next: e.target.value })} />
              <div className="mt-2 h-[3px] w-full bg-gray-100 rounded overflow-hidden">
                <div className={cn('h-full transition-all duration-[300ms]', meta.color)} style={{ width: `${meta.w}%` }} />
              </div>
              {meta.label && <div className="mt-1 text-xs text-gray-600">{meta.label}</div>}
            </div>
          </div>
          <div className="flex justify-end">
            <Button onClick={savePassword} loading={updatePassword.isPending}>{t('profile.changePassword')}</Button>
          </div>
        </section>
      </PageTransition>
    </AppShell>
  )
}
