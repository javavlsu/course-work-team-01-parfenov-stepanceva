import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Plus, Check, X } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { PageTransition } from '../components/layout/PageTransition'
import { Button } from '../components/ui/Button'
import { GroupCard } from '../components/groups/GroupCard'
import { GroupCreateModal } from '../components/groups/GroupCreateModal'
import { useGroups } from '../hooks/useGroups'
import { useMyInvitations, useRespondInvitation } from '../hooks/useInvitations'
import { useAuthStore } from '../store/authStore'

export default function DashboardPage() {
  const currentUser = useAuthStore((s) => s.user)
  const { data: groups = [], isLoading } = useGroups()
  const { data: invitations = [] } = useMyInvitations()
  const respond = useRespondInvitation()
  const [open, setOpen] = useState(false)
  const [bannerOpen, setBannerOpen] = useState(true)
  const [expandInvites, setExpandInvites] = useState(false)
  const nav = useNavigate()

  const pending = invitations.filter((i) => i.status === 'PENDING')

  return (
    <AppShell>
      <PageTransition className="max-w-container mx-auto px-6 md:px-12 py-10">
        {bannerOpen && pending.length > 0 && (
          <motion.div
            initial={{ y: -24, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ duration: 0.4, delay: 0.2 }}
            className="mb-8 p-4 bg-accent-2 rounded-md"
          >
            <div className="flex items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <span className="text-xl">📬</span>
                <span className="text-sm">У вас {pending.length} приглашен{pending.length === 1 ? 'ие' : pending.length < 5 ? 'ия' : 'ий'} в команду</span>
              </div>
              <div className="flex items-center gap-2">
                <Button size="sm" variant="secondary" onClick={() => setExpandInvites((v) => !v)}>
                  {expandInvites ? 'Скрыть' : 'Посмотреть'}
                </Button>
                <button onClick={() => setBannerOpen(false)} className="w-8 h-8 inline-flex items-center justify-center rounded-md hover:bg-ink/10">✕</button>
              </div>
            </div>
            {expandInvites && (
              <div className="mt-4 space-y-2">
                {pending.map((inv) => (
                  <div key={inv.id} className="bg-paper rounded-md p-3 flex items-center justify-between gap-3">
                    <div>
                      <div className="text-sm font-medium">{inv.group?.name || 'Группа'}</div>
                      <div className="text-xs text-gray-600">От: {inv.createdBy?.username}</div>
                    </div>
                    <div className="flex items-center gap-1">
                      <button
                        onClick={() => respond.mutate({ id: inv.id, accept: true })}
                        disabled={respond.isPending}
                        className="w-8 h-8 inline-flex items-center justify-center rounded-md bg-status-done text-paper hover:opacity-90"
                        aria-label="Принять"
                      >
                        <Check className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => respond.mutate({ id: inv.id, accept: false })}
                        disabled={respond.isPending}
                        className="w-8 h-8 inline-flex items-center justify-center rounded-md bg-priority-high text-paper hover:opacity-90"
                        aria-label="Отклонить"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </motion.div>
        )}

        <div className="flex flex-wrap items-end justify-between gap-4 mb-10">
          <div>
            <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-2">Dashboard</div>
            <h1 className="display-serif text-3xl md:text-4xl leading-none">Привет, {currentUser?.username}!</h1>
          </div>
          <Button icon={Plus} size="lg" onClick={() => setOpen(true)}>Создать группу</Button>
        </div>

        {isLoading ? (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {[0, 1, 2].map((i) => (
              <div key={i} className="skeleton h-[180px] rounded-lg" />
            ))}
          </div>
        ) : groups.length === 0 ? (
          <EmptyState onCreate={() => setOpen(true)} />
        ) : (
          <motion.div
            initial="initial"
            animate="animate"
            variants={{ animate: { transition: { staggerChildren: 0.06, delayChildren: 0.1 } } }}
            className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6"
          >
            {groups.map((g, i) => (
              <GroupCard key={g.id} group={g} index={i} />
            ))}
          </motion.div>
        )}
      </PageTransition>

      <GroupCreateModal open={open} onClose={() => setOpen(false)} onCreated={(g) => nav(`/groups/${g.id}`)} />
    </AppShell>
  )
}

function EmptyState({ onCreate }) {
  return (
    <div className="border border-dashed border-gray-200 rounded-lg p-16 text-center">
      <h2 className="display-serif text-2xl mb-2">Здесь пока пусто</h2>
      <p className="text-gray-600 mb-6">Создайте первую группу, чтобы начать работу.</p>
      <Button onClick={onCreate} icon={Plus}>Создать группу</Button>
    </div>
  )
}
