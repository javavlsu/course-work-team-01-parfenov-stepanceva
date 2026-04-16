import { useRef, useState } from 'react'
import { Camera } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { PageTransition } from '../components/layout/PageTransition'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Avatar } from '../components/ui/Avatar'
import { useMockStore } from '../store/mockStore'
import { useAuthStore } from '../store/authStore'
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
const strengthMeta = [
  { w: 0, color: 'bg-gray-200', label: '' },
  { w: 25, color: 'bg-priority-high', label: 'Слабый' },
  { w: 50, color: 'bg-priority-medium', label: 'Средний' },
  { w: 75, color: 'bg-status-progress', label: 'Хороший' },
  { w: 100, color: 'bg-status-done', label: 'Сильный' },
]

export default function ProfilePage() {
  const currentUser = useMockStore((s) => s.currentUser)
  const updateUser = useMockStore((s) => s.updateUser)
  const setAuthUser = useAuthStore((s) => s.setUser)
  const fileRef = useRef(null)

  const [username, setUsername] = useState(currentUser.username)
  const [avatarPreview, setAvatarPreview] = useState(null)
  const [avatarData, setAvatarData] = useState(null)
  const [pw, setPw] = useState({ current: '', next: '' })
  const meta = strengthMeta[scorePassword(pw.next)]

  const onFile = (file) => {
    if (!file) return
    const reader = new FileReader()
    reader.onload = () => { setAvatarPreview(reader.result); setAvatarData(reader.result) }
    reader.readAsDataURL(file)
  }

  const applyAvatar = () => {
    updateUser({ avatar: avatarData })
    setAuthUser({ ...currentUser, avatar: avatarData })
    setAvatarPreview(null); setAvatarData(null)
    toast.success('Аватар обновлён')
  }

  const saveUsername = () => {
    if (!username.trim()) return toast.error('Введите имя')
    updateUser({ username: username.trim() })
    setAuthUser({ ...currentUser, username: username.trim() })
    toast.success('Имя сохранено')
  }

  const savePassword = () => {
    if (!pw.current || !pw.next) return toast.error('Заполните оба поля')
    if (pw.next.length < 8) return toast.error('Минимум 8 символов')
    setPw({ current: '', next: '' })
    toast.success('Пароль обновлён')
  }

  return (
    <AppShell breadcrumb={[{ label: 'Профиль' }]}>
      <PageTransition className="max-w-3xl mx-auto px-6 md:px-12 py-10">
        <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-2">Profile</div>
        <h1 className="display-serif text-3xl md:text-4xl mb-10">Настройки</h1>

        <section className="bg-paper border border-gray-100 rounded-lg p-8 mb-8">
          <div className="flex flex-col sm:flex-row items-start gap-8">
            <div className="relative group">
              <Avatar user={{ ...currentUser, avatar: avatarPreview || currentUser.avatar }} size="2xl" />
              <button
                onClick={() => fileRef.current?.click()}
                aria-label="Изменить фото"
                className="absolute inset-0 rounded-full flex items-center justify-center bg-ink/60 text-paper opacity-0 group-hover:opacity-100 transition-opacity"
              >
                <Camera className="w-6 h-6" />
              </button>
              <input ref={fileRef} type="file" accept="image/*" className="hidden" onChange={(e) => onFile(e.target.files?.[0])} />
            </div>
            <div className="flex-1 space-y-2">
              <h2 className="display-serif text-2xl">{currentUser.username}</h2>
              <p className="text-gray-600">{currentUser.email}</p>
              <p className="text-xs text-gray-400">В системе с: {formatDate(currentUser.createdAt)}</p>
              {avatarPreview && (
                <div className="flex gap-2 pt-3">
                  <Button size="sm" onClick={applyAvatar}>Применить</Button>
                  <Button size="sm" variant="ghost" onClick={() => { setAvatarPreview(null); setAvatarData(null) }}>Отмена</Button>
                </div>
              )}
            </div>
          </div>
        </section>

        <section className="bg-paper border border-gray-100 rounded-lg p-8 mb-8">
          <h3 className="display-serif text-xl mb-4">Изменить имя</h3>
          <div className="flex flex-col sm:flex-row gap-3 items-stretch sm:items-end">
            <div className="flex-1">
              <Input label="Имя пользователя" value={username} onChange={(e) => setUsername(e.target.value)} />
            </div>
            <Button onClick={saveUsername}>Сохранить</Button>
          </div>
        </section>

        <section className="bg-paper border border-gray-100 rounded-lg p-8">
          <h3 className="display-serif text-xl mb-4">Изменить пароль</h3>
          <div className="grid sm:grid-cols-2 gap-3 mb-3">
            <Input type="password" label="Текущий пароль" value={pw.current} onChange={(e) => setPw({ ...pw, current: e.target.value })} />
            <div>
              <Input type="password" label="Новый пароль" value={pw.next} onChange={(e) => setPw({ ...pw, next: e.target.value })} />
              <div className="mt-2 h-[3px] w-full bg-gray-100 rounded overflow-hidden">
                <div className={cn('h-full transition-all duration-[300ms]', meta.color)} style={{ width: `${meta.w}%` }} />
              </div>
              {meta.label && <div className="mt-1 text-xs text-gray-600">{meta.label}</div>}
            </div>
          </div>
          <div className="flex justify-end">
            <Button onClick={savePassword}>Обновить</Button>
          </div>
        </section>
      </PageTransition>
    </AppShell>
  )
}
