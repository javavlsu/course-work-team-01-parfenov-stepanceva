import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, MoreHorizontal, Trash2, Users, Pencil } from 'lucide-react'
import { AppShell } from '../components/layout/AppShell'
import { KanbanBoard } from '../components/kanban/KanbanBoard'
import { TaskModal } from '../components/tasks/TaskModal'
import { AvatarGroup } from '../components/ui/Avatar'
import { Dropdown } from '../components/ui/Dropdown'
import { Button } from '../components/ui/Button'
import { useMockStore } from '../store/mockStore'
import { toast } from 'sonner'

export default function BoardPage() {
  const { boardId } = useParams()
  const nav = useNavigate()
  const board = useMockStore((s) => s.boards[boardId])
  const group = useMockStore((s) => board ? s.groups[board.groupId] : null)
  const users = useMockStore((s) => s.users)
  const removeBoard = useMockStore((s) => s.removeBoard)
  const [activeTaskId, setActiveTaskId] = useState(null)

  if (!board || !group) {
    return (
      <AppShell>
        <div className="p-12 text-center">
          <h2 className="display-serif text-2xl mb-2">Доска не найдена</h2>
          <Button onClick={() => nav('/dashboard')}>К дашборду</Button>
        </div>
      </AppShell>
    )
  }

  const members = group.members.map((m) => users[m.userId]).filter(Boolean)
  const breadcrumb = [
    { label: 'Dashboard', to: '/dashboard' },
    { label: group.name, to: `/groups/${group.id}` },
    { label: board.name },
  ]

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
            <Link to={`/groups/${group.id}`} className="w-9 h-9 inline-flex items-center justify-center rounded-md hover:bg-gray-100 transition-colors" aria-label="Назад">
              <ArrowLeft className="w-4 h-4" />
            </Link>
            <div className="min-w-0">
              <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400 truncate">{group.name}</div>
              <h1 className="display-serif text-2xl truncate leading-tight">{board.name}</h1>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="hidden sm:flex items-center gap-2">
              <AvatarGroup users={members} max={5} size="sm" />
              <Button variant="outline" size="sm" icon={Users} onClick={() => nav(`/groups/${group.id}`)}>Участники</Button>
            </div>
            <Dropdown
              align="right"
              trigger={
                <button className="w-9 h-9 inline-flex items-center justify-center rounded-md border border-gray-200 hover:border-ink" aria-label="More">
                  <MoreHorizontal className="w-4 h-4" />
                </button>
              }
              items={[
                { label: 'Переименовать', icon: Pencil, onClick: () => toast.info('Скоро') },
                { label: 'Удалить доску', icon: Trash2, danger: true, onClick: () => {
                  if (confirm('Удалить доску?')) { removeBoard(boardId); toast.success('Удалена'); nav(`/groups/${group.id}`) }
                } },
              ]}
            />
          </div>
        </motion.div>

        <div className="flex-1 overflow-hidden pt-4">
          <KanbanBoard boardId={boardId} onOpenTask={(t) => setActiveTaskId(t.id)} />
        </div>
      </div>

      <TaskModal taskId={activeTaskId} open={!!activeTaskId} onClose={() => setActiveTaskId(null)} />
    </AppShell>
  )
}
