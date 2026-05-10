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
import { useTranslation } from '../i18n'
import { cn } from '../utils/cn'

export default function GroupPage() {
  const { groupId } = useParams()
  const nav = useNavigate()
  const currentUser = useAuthStore((s) => s.user)
  const { data: group, isLoading: groupLoading, isError } = useGroup(groupId)
  const { data: members = [] } = useGroupMembers(groupId)
  const { data: boards = [], isLoading: boardsLoading } = useGroupBoards(groupId)
  const deleteGroup = useDeleteGroup()
  const { t } = useTranslation()

  const [tab, setTab] = useState('boards')
  const [boardOpen, setBoardOpen] = useState(false)

  const TABS = [
    { id: 'boards', label: t('groups.boards') },
    { id: 'members', label: t('groups.members') },
    { id: 'invitations', label: t('groups.invitations') },
  ]

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
          <h2 className="display-serif text-2xl mb-2">{t('common.notFound')}</h2>
          <Button onClick={() => nav('/dashboard')}>{t('boards.toDashboard')}</Button>
        </div>
      </AppShell>
    )
  }

  const myRole = members.find((m) => m.userId === currentUser?.id)?.role
  const isAdmin = myRole === 'ADMIN'
  const memberStubs = members.map((m) => ({ id: m.userId, username: `#${m.userId}` }))

  const breadcrumb = [{ label: t('nav.dashboard'), to: '/dashboard' }, { label: group.name }]

  return (
    <AppShell breadcrumb={breadcrumb}>
      <PageTransition className="max-w-container mx-auto px-6 md:px-12 py-10">
        <div className="flex flex-wrap items-start justify-between gap-4 mb-8">
          <div>
            <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-2">{t('groups.title')}</div>
            <h1 className="display-serif text-3xl md:text-4xl uppercase tracking-tight leading-none mb-2">{group.name}</h1>
            {group.description && <p className="text-gray-600 max-w-xl">{group.description}</p>}
            <div className="mt-5 flex items-center gap-4">
              <AvatarGroup users={memberStubs} max={6} size="sm" />
              <span className="text-xs mono text-gray-400">
                {members.filter((m) => m.role === 'ADMIN').length} {t('groups.roleAdmin').toLowerCase()} · {members.length} {t('common.all').toLowerCase()}
              </span>
            </div>
          </div>
          <div className="flex items-center gap-2">
            {isAdmin && <Button icon={Plus} onClick={() => setBoardOpen(true)}>{t('boards.create')}</Button>}
            {isAdmin && (
              <Dropdown
                align="right"
                trigger={
                  <button className="w-10 h-10 inline-flex items-center justify-center rounded-md border border-gray-200 hover:border-ink transition-colors" aria-label={t('common.actions')}>
                    <MoreHorizontal className="w-4 h-4" />
                  </button>
                }
                items={[
                  { label: t('common.delete'), icon: Trash2, danger: true, onClick: () => {
                    if (confirm(t('groups.deleteConfirm'))) {
                      deleteGroup.mutate(groupId, { onSuccess: () => nav('/dashboard') })
                    }
                  } },
                ]}
              />
            )}
          </div>
        </div>

        <div className="flex gap-6 border-b border-gray-100 mb-8 overflow-x-auto no-scrollbar">
          {TABS.map((tabItem) => (
            <button
              key={tabItem.id}
              onClick={() => setTab(tabItem.id)}
              className={cn(
                'py-3 relative text-sm transition-colors whitespace-nowrap',
                tab === tabItem.id ? 'text-ink' : 'text-gray-600 hover:text-ink'
              )}
            >
              {tabItem.label}
              {tab === tabItem.id && (
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
                <h2 className="display-serif text-2xl mb-2">{t('dashboard.noBoards')}</h2>
                {isAdmin && <Button onClick={() => setBoardOpen(true)} icon={Plus}>{t('boards.create')}</Button>}
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
                <p className="text-gray-600">{t('groups.notMember')}</p>
              </div>
            )}
          </div>
        )}
      </PageTransition>

      <BoardCreateModal open={boardOpen} onClose={() => setBoardOpen(false)} groupId={groupId} />
    </AppShell>
  )
}
