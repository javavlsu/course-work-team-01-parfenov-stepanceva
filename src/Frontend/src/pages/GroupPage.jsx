import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Plus, MoreHorizontal, Trash2 } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { PageTransition } from '../components/layout/PageTransition'
import { Button } from '../components/ui/Button'
import { AvatarGroup } from '../components/ui/Avatar'
import { Dropdown } from '../components/ui/Dropdown'
import { BoardCard } from '../components/boards/BoardCard'
import { BoardCreateModal } from '../components/boards/BoardCreateModal'
import { MemberList } from '../components/groups/MemberList'
import { InviteForm } from '../components/invitations/InviteForm'
import { useGroup, useGroupMembers, useDeleteGroup } from '../hooks/useGroups'
import { useGroupBoards } from '../hooks/useBoards'
import { useAuthStore } from '../store/authStore'
import { cn } from '../utils/cn'

const TABS = [
  { id: 'boards', label: 'Доски' },
  { id: 'members', label: 'Участники' },
  { id: 'invitations', label: 'Приглашения' },
]

export default function GroupPage() {
  const { groupId } = useParams()
  const nav = useNavigate()
  const currentUser = useAuthStore((s) => s.user)
  const { data: group, isLoading: groupLoading, isError } = useGroup(groupId)
  const { data: members = [] } = useGroupMembers(groupId)
  const { data: boards = [], isLoading: boardsLoading } = useGroupBoards(groupId)
  const deleteGroup = useDeleteGroup()

  const [tab, setTab] = useState('boards')
  const [boardOpen, setBoardOpen] = useState(false)

  if (groupLoading) {
    return (
      <AppShell>
        <div className="p-12 flex justify-center">
          <div className="w-10 h-10 rounded-full border-2 border-gray-200 border-t-ink animate-spin" />
        </div>
      </AppShell>
    )
  }

  if (isError || !group) {
    return (
      <AppShell>
        <div className="p-12 text-center">
          <h2 className="display-serif text-2xl mb-2">Группа не найдена</h2>
          <Button onClick={() => nav('/dashboard')}>К списку</Button>
        </div>
      </AppShell>
    )
  }

  const myRole = members.find((m) => m.userId === currentUser?.id)?.role
  const isAdmin = myRole === 'ADMIN'
  const memberStubs = members.map((m) => ({ id: m.userId, username: `#${m.userId}` }))

  const breadcrumb = [{ label: 'Dashboard', to: '/dashboard' }, { label: group.name }]

  return (
    <AppShell breadcrumb={breadcrumb}>
      <PageTransition className="max-w-container mx-auto px-6 md:px-12 py-10">
        <div className="flex flex-wrap items-start justify-between gap-4 mb-8">
          <div>
            <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-2">Team</div>
            <h1 className="display-serif text-3xl md:text-4xl uppercase tracking-tight leading-none mb-2">{group.name}</h1>
            {group.description && <p className="text-gray-600 max-w-xl">{group.description}</p>}
            <div className="mt-5 flex items-center gap-4">
              <AvatarGroup users={memberStubs} max={6} size="sm" />
              <span className="text-xs mono text-gray-400">
                {members.filter((m) => m.role === 'ADMIN').length} admin · {members.length} всего
              </span>
            </div>
          </div>
          <div className="flex items-center gap-2">
            {isAdmin && <Button icon={Plus} onClick={() => setBoardOpen(true)}>Доска</Button>}
            {isAdmin && (
              <Dropdown
                align="right"
                trigger={
                  <button className="w-10 h-10 inline-flex items-center justify-center rounded-md border border-gray-200 hover:border-ink transition-colors" aria-label="More">
                    <MoreHorizontal className="w-4 h-4" />
                  </button>
                }
                items={[
                  { label: 'Удалить группу', icon: Trash2, danger: true, onClick: () => {
                    if (confirm('Удалить группу?')) {
                      deleteGroup.mutate(groupId, { onSuccess: () => nav('/dashboard') })
                    }
                  } },
                ]}
              />
            )}
          </div>
        </div>

        <div className="flex gap-6 border-b border-gray-100 mb-8 overflow-x-auto no-scrollbar">
          {TABS.map((t) => (
            <button
              key={t.id}
              onClick={() => setTab(t.id)}
              className={cn(
                'py-3 relative text-sm transition-colors whitespace-nowrap',
                tab === t.id ? 'text-ink' : 'text-gray-600 hover:text-ink'
              )}
            >
              {t.label}
              {tab === t.id && (
                <motion.span
                  layoutId="tab-underline"
                  className="absolute left-0 right-0 -bottom-px h-[2px] bg-ink"
                />
              )}
            </button>
          ))}
        </div>

        {tab === 'boards' && (
          <>
            {boardsLoading ? (
              <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {[0, 1, 2].map((i) => <div key={i} className="skeleton aspect-[3/2] rounded-lg" />)}
              </div>
            ) : boards.length === 0 ? (
              <div className="border border-dashed border-gray-200 rounded-lg p-16 text-center">
                <h2 className="display-serif text-2xl mb-2">Пока нет досок</h2>
                <p className="text-gray-600 mb-6">
                  {isAdmin ? 'Создайте первую доску, чтобы начать работу.' : 'Администратор пока не создал досок.'}
                </p>
                {isAdmin && <Button onClick={() => setBoardOpen(true)} icon={Plus}>Создать доску</Button>}
              </div>
            ) : (
              <motion.div
                initial="initial"
                animate="animate"
                variants={{ animate: { transition: { staggerChildren: 0.06 } } }}
                className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6"
              >
                {boards.map((b, i) => <BoardCard key={b.id} board={b} index={i} />)}
              </motion.div>
            )}
          </>
        )}

        {tab === 'members' && <MemberList groupId={groupId} isAdmin={isAdmin} />}

        {tab === 'invitations' && (
          <div className="space-y-6">
            {isAdmin ? (
              <InviteForm groupId={groupId} />
            ) : (
              <div className="border border-dashed border-gray-200 rounded-lg p-12 text-center">
                <p className="text-gray-600">Только администратор группы может приглашать участников.</p>
              </div>
            )}
          </div>
        )}
      </PageTransition>

      <BoardCreateModal open={boardOpen} onClose={() => setBoardOpen(false)} groupId={groupId} />
    </AppShell>
  )
}
