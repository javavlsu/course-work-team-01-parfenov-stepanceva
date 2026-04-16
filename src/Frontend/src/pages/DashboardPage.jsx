import { useState } from 'react'
import { motion } from 'framer-motion'
import { Plus } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { PageTransition } from '../components/layout/PageTransition'
import { Button } from '../components/ui/Button'
import { GroupCard } from '../components/groups/GroupCard'
import { GroupCreateModal } from '../components/groups/GroupCreateModal'
import { useMockStore } from '../store/mockStore'
import { Badge } from '../components/ui/Badge'

export default function DashboardPage() {
  const currentUser = useMockStore((s) => s.currentUser)
  const groups = useMockStore((s) => s.listGroups())
  const [open, setOpen] = useState(false)
  const [bannerOpen, setBannerOpen] = useState(true)

  return (
    <AppShell>
      <PageTransition className="max-w-container mx-auto px-6 md:px-12 py-10">
        {bannerOpen && (
          <motion.div
            initial={{ y: -24, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ duration: 0.4, delay: 1 }}
            className="mb-8 flex items-center justify-between gap-4 p-4 bg-accent-2 rounded-md"
          >
            <div className="flex items-center gap-3">
              <span className="text-xl">📬</span>
              <span className="text-sm">У вас 2 приглашения в команду</span>
            </div>
            <div className="flex items-center gap-2">
              <Button size="sm" variant="secondary">Посмотреть</Button>
              <button onClick={() => setBannerOpen(false)} className="w-8 h-8 inline-flex items-center justify-center rounded-md hover:bg-ink/10">✕</button>
            </div>
          </motion.div>
        )}

        <div className="flex flex-wrap items-end justify-between gap-4 mb-10">
          <div>
            <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-2">Dashboard</div>
            <h1 className="display-serif text-3xl md:text-4xl leading-none">Привет, {currentUser?.username}!</h1>
          </div>
          <Button icon={Plus} size="lg" onClick={() => setOpen(true)}>Создать группу</Button>
        </div>

        {groups.length === 0 ? (
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

      <GroupCreateModal open={open} onClose={() => setOpen(false)} />
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
