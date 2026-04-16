import { useState } from 'react'
import { Copy, Check, Link2 } from 'lucide-react'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'
import { toast } from 'sonner'

export function InviteForm({ groupId }) {
  const [email, setEmail] = useState('')
  const [expiry, setExpiry] = useState('7')
  const [link, setLink] = useState('')
  const [copied, setCopied] = useState(false)

  const sendEmail = (e) => {
    e.preventDefault()
    if (!email) return toast.error('Введите email')
    toast.success(`Приглашение отправлено на ${email}`)
    setEmail('')
  }

  const genLink = () => {
    const token = Math.random().toString(36).slice(2, 12)
    const full = `${window.location.origin}/join/${token}`
    setLink(full)
    toast.success('Ссылка создана')
  }

  const copy = async () => {
    await navigator.clipboard.writeText(link)
    setCopied(true)
    setTimeout(() => setCopied(false), 1500)
  }

  return (
    <div className="grid md:grid-cols-2 gap-6">
      <div className="bg-paper border border-gray-100 rounded-lg p-6">
        <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 mb-4">По email</div>
        <form onSubmit={sendEmail} className="space-y-3">
          <Input type="email" label="Email участника" value={email} onChange={(e) => setEmail(e.target.value)} />
          <div>
            <div className="text-xs text-gray-600 mb-2">Срок действия</div>
            <div className="flex gap-2 flex-wrap">
              {['1', '3', '7', '14', '30'].map((d) => (
                <button
                  key={d}
                  type="button"
                  onClick={() => setExpiry(d)}
                  className={`px-3 py-1.5 rounded-md text-xs mono transition-colors ${
                    expiry === d ? 'bg-ink text-paper' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                >
                  {d}d
                </button>
              ))}
            </div>
          </div>
          <Button type="submit" fullWidth>Отправить приглашение</Button>
        </form>
      </div>

      <div className="bg-paper border border-gray-100 rounded-lg p-6">
        <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 mb-4">По ссылке</div>
        {!link ? (
          <div className="space-y-4">
            <p className="text-sm text-gray-600">Создайте универсальную ссылку для приглашения.</p>
            <Button onClick={genLink} variant="outline" fullWidth icon={Link2}>Создать ссылку</Button>
          </div>
        ) : (
          <div className="space-y-3">
            <div className="bg-gray-100 rounded-md p-3 text-xs mono break-all">{link}</div>
            <div className="flex gap-2">
              <Button onClick={copy} fullWidth icon={copied ? Check : Copy}>
                {copied ? 'Скопировано' : 'Копировать'}
              </Button>
              <Button onClick={genLink} variant="ghost">Новая</Button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
