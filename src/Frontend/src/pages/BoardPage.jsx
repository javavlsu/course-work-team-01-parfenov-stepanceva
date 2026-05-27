import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, MoreHorizontal, Trash2, Users, Pencil, UserCog, LayoutGrid, List } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { KanbanBoard } from '../components/kanban/KanbanBoard'
import { TaskModal } from '../components/tasks/TaskModal'
import { TasksTable } from '../components/tasks/TasksTable'
import { BoardMembersModal } from '../components/boards/BoardMembersModal'
import { AvatarGroup } from '../components/ui/Avatar'
import { Dropdown } from '../components/ui/Dropdown'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { useBoard, useBoardUsers, useDeleteBoard, useUpdateBoard } from '../hooks/useBoards'
import { useTranslation } from '../i18n'
import { cn } from '../utils/cn'
import { toast } from 'sonner'

export default function BoardPage() {
  const { boardId } = useParams()
  const nav = useNavigate()
  const { data: board, isLoading, isError } = useBoard(boardId)
  const { data: boardUsers = [] } = useBoardUsers(boardId)
  const deleteBoard = useDeleteBoard()
  const updateBoard = useUpdateBoard()
  const { t } = useTranslation()

  const [activeTaskId, setActiveTaskId] = useState(null)
  const [editOpen, setEditOpen] = useState(false)
  const [editTitle, setEditTitle] = useState('')
  const [editDesc, setEditDesc] = useState('')
  const [membersOpen, setMembersOpen] = useState(false)
  const [view, setView] = useState('board') // 'board' | 'list'

  if (isLoading) {
    return (
      <AppShell>
        <div className="p-12 flex justify-center">
          <div className="w-10 h-10 rounded-full border-2 border-gray-200 border-t-ink animate-spin" />
        </div>
      </AppShell>
    )
  }

  if (isError || !board) {
    return (
      <AppShell>
        <div className="p-12 text-center">
          <h2 className="display-serif text-2xl mb-2">{t('boards.notFound')}</h2>
          <Button onClick={() => nav('/dashboard')}>{t('boards.toDashboard')}</Button>
        </div>
      </AppShell>
    )
  }

  const breadcrumb = [
    { label: t('nav.dashboard'), to: '/dashboard' },
    { label: board.groupName || t('groups.title'), to: `/groups/${board.groupId}` },
    { label: board.name },
  ]

  const openEdit = () => {
    setEditTitle(board.name)
    setEditDesc(board.description || '')
    setEditOpen(true)
  }

  const saveEdit = (e) => {
    e.preventDefault()
    if (editTitle.trim().length < 3) return toast.error(t('boards.minTitle'))
    updateBoard.mutate({ id: board.id, title: editTitle.trim(), description: editDesc.trim() }, {
      onSuccess: () => setEditOpen(false),
    })
  }

  return (
    <AppShell breadcrumb={breadcrumb}>
      <div className="h-[calc(100vh-64px)] flex flex-col">
        <motion.div
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="flex items-center justify-between gap-4 px-6 md:px-12 py-5 border-b border-gray-100"
        >
          <div className="flex items-center gap-3 min-w-0">
            <Link to={`/groups/${board.groupId}`} className="w-9 h-9 inline-flex items-center justify-center rounded-md hover:bg-gray-100 transition-colors" aria-label="Назад">
              <ArrowLeft className="w-4 h-4" />
            </Link>
            <div className="min-w-0">
              <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 truncate">{board.groupName || 'Группа'}</div>
              <h1 className="display-serif text-2xl truncate leading-tight">{board.name}</h1>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="hidden sm:flex items-center gap-2">
              <AvatarGroup users={boardUsers} max={5} size="sm" />
              <Button variant="outline" size="sm" icon={Users} onClick={() => setMembersOpen(true)}>{t('common.members')}</Button>
            </div>
            <Dropdown
              align="right"
              trigger={
                <button className="w-9 h-9 inline-flex items-center justify-center rounded-md border border-gray-200 hover:border-ink" aria-label={t('common.actions')}>
                  <MoreHorizontal className="w-4 h-4" />
                </button>
              }
              items={[
                { label: t('common.rename'), icon: Pencil, onClick: openEdit },
                { label: t('boards.membersTitle'), icon: UserCog, onClick: () => setMembersOpen(true) },
                { label: t('common.delete'), icon: Trash2, danger: true, onClick: () => {
                  if (confirm(t('boards.deleteConfirm'))) {
                    deleteBoard.mutate(boardId, { onSuccess: () => nav(`/groups/${board.groupId}`) })
                  }
                } },
              ]}
            />
          </div>
        </motion.div>

        <div className="px-6 md:px-12 pt-4 border-b border-gray-100">
          <div className="flex items-center gap-5">
            {[
              { id: 'board', label: t('boards.tabBoard'), icon: LayoutGrid },
              { id: 'list', label: t('boards.tabTasks'), icon: List },
            ].map((tabItem) => (
              <button
                key={tabItem.id}
                onClick={() => setView(tabItem.id)}
                className={cn(
                  'pb-3 relative mono text-xs tracking-[0.1em] uppercase transition-colors inline-flex items-center gap-1.5',
                  view === tabItem.id ? 'text-ink' : 'text-gray-400 hover:text-gray-600'
                )}
              >
                <tabItem.icon className="w-3.5 h-3.5" />
                {tabItem.label}
                {view === tabItem.id && <span className="absolute left-0 right-0 -bottom-px h-[2px] bg-ink" />}
              </button>
            ))}
          </div>
        </div>

        <div className="flex-1 overflow-hidden pt-4">
          {view === 'board' ? (
            <KanbanBoard boardId={boardId} onOpenTask={(t) => setActiveTaskId(t.id)} />
          ) : (
            <TasksTable boardId={boardId} onOpenTask={(t) => setActiveTaskId(t.id)} />
          )}
        </div>
      </div>

      <TaskModal boardId={boardId} taskId={activeTaskId} open={!!activeTaskId} onClose={() => setActiveTaskId(null)} />

      <BoardMembersModal open={membersOpen} onClose={() => setMembersOpen(false)} boardId={boardId} groupId={board.groupId} />

      <Modal open={editOpen} onClose={() => setEditOpen(false)} title={t('boards.rename')} size="sm">
        <form onSubmit={saveEdit} className="px-6 py-5 space-y-4">
          <Input label={t('boards.nameLabel')} value={editTitle} onChange={(e) => setEditTitle(e.target.value)} autoFocus />
          <Input label={t('boards.descLabel')} value={editDesc} onChange={(e) => setEditDesc(e.target.value)} />
          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" type="button" onClick={() => setEditOpen(false)}>{t('common.cancel')}</Button>
            <Button type="submit" loading={updateBoard.isPending}>{t('common.save')}</Button>
          </div>
        </form>
      </Modal>
    </AppShell>
  )
}
