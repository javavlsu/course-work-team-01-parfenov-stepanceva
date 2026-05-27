import { useState } from 'react'
import { Copy, Check, Link2, Trash2 } from 'lucide-react'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'
import { Badge } from '../ui/Badge'
import {
  useInviteByEmail, useInviteByLink, useGroupInvitations, useCancelInvitation,
} from '../../hooks/useInvitations'
import { formatDate } from '../../utils/dates'
import { useTranslation } from '../../i18n'

export function InviteForm({ groupId }) {
  const [email, setEmail] = useState('')
  const [expiry, setExpiry] = useState(7)
  const [copied, setCopied] = useState(null)
  const { t } = useTranslation()

  const inviteEmail = useInviteByEmail(groupId)
  const inviteLink = useInviteByLink(groupId)
  const cancelInvite = useCancelInvitation(groupId)
  const { data: invites = [], isLoading } = useGroupInvitations(groupId)

  const sendEmail = (e) => {
    e.preventDefault()
    if (!email) return
    inviteEmail.mutate({ email, expiresInDays: expiry }, { onSuccess: () => setEmail('') })
  }

  const genLink = () => inviteLink.mutate(expiry)

  const copy = async (token, id) => {
    const full = `${window.location.origin}/join/${token}`
    await navigator.clipboard.writeText(full)
    setCopied(id)
    setTimeout(() => setCopied(null), 1500)
  }

  return (
    <div className="space-y-6">
      <div className="grid md:grid-cols-2 gap-6">
        <div className="bg-paper border border-gray-100 rounded-lg p-6">
          <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 mb-4">{t('groups.inviteByEmail')}</div>
          <form onSubmit={sendEmail} className="space-y-3">
            <Input type="email" label={t('groups.emailPlaceholder')} value={email} onChange={(e) => setEmail(e.target.value)} />
            <div>
              <div className="text-xs text-gray-600 mb-2">{t('groups.linkLifetime')}</div>
              <div className="flex gap-2 flex-wrap">
                {[1, 3, 7, 14, 30].map((d) => (
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
            <Button type="submit" fullWidth loading={inviteEmail.isPending}>{t('groups.invite')}</Button>
          </form>
        </div>

        <div className="bg-paper border border-gray-100 rounded-lg p-6">
          <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 mb-4">{t('groups.inviteByLink')}</div>
          <Button onClick={genLink} variant="outline" fullWidth icon={Link2} loading={inviteLink.isPending}>{t('groups.inviteByLink')}</Button>
        </div>
      </div>

      <div>
        <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 mb-3">{t('groups.invitations')}</div>
        {isLoading ? (
          <div className="skeleton h-24 rounded-lg" />
        ) : invites.length === 0 ? (
          <div className="border border-dashed border-gray-200 rounded-lg p-8 text-center text-sm text-gray-500">{t('common.none')}</div>
        ) : (
          <div className="bg-paper border border-gray-100 rounded-lg overflow-hidden">
            {invites.map((inv) => {
              const fullLink = inv.type === 'LINK' && inv.inviteToken ? `${window.location.origin}/join/${inv.inviteToken}` : null
              return (
                <div key={inv.id} className="flex items-center gap-4 px-4 py-3 border-t first:border-t-0 border-gray-100">
                  <Badge variant={inv.type === 'EMAIL' ? 'default' : 'ink'}>{inv.type}</Badge>
                  <div className="flex-1 min-w-0 text-sm">
                    {inv.type === 'EMAIL' ? (
                      <div className="truncate">{inv.email}</div>
                    ) : (
                      <div className="truncate mono text-xs">{fullLink}</div>
                    )}
                    <div className="text-xs text-gray-400">
                      {inv.status} · {t('invitations.expiresOn')} {formatDate(inv.expiresAt)}
                    </div>
                  </div>
                  {fullLink && (
                    <button
                      onClick={() => copy(inv.inviteToken, inv.id)}
                      className="w-8 h-8 inline-flex items-center justify-center rounded-md hover:bg-gray-100"
                      aria-label={t('groups.copyLink')}
                    >
                      {copied === inv.id ? <Check className="w-4 h-4 text-status-done" /> : <Copy className="w-4 h-4" />}
                    </button>
                  )}
                  <button
                    onClick={() => { if (confirm(t('common.confirm'))) cancelInvite.mutate(inv.id) }}
                    className="w-8 h-8 inline-flex items-center justify-center rounded-md text-gray-400 hover:text-priority-high hover:bg-priority-high/10"
                    aria-label={t('common.delete')}
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}
