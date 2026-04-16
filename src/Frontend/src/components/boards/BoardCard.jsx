import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useMockStore } from '../../store/mockStore'
import { formatDate } from '../../utils/dates'
import { cn } from '../../utils/cn'

export function BoardCard({ board, index = 0 }) {
  const tasks = useMockStore((s) => {
    const cols = Object.values(s.columns).filter((c) => c.boardId === board.id).map((c) => c.id)
    return Object.values(s.tasks).filter((t) => cols.includes(t.columnId))
  })
  const author = useMockStore((s) => s.users[board.authorId])
  const inProgress = tasks.filter((t) => {
    const col = useMockStore.getState().columns[t.columnId]
    return col?.name?.toLowerCase().includes('progress')
  }).length

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: index * 0.06, ease: [0.16, 1, 0.3, 1] }}
      whileHover={{ y: -3 }}
    >
      <Link
        to={`/boards/${board.id}`}
        className="group relative block overflow-hidden bg-paper border border-gray-100 rounded-lg aspect-[3/2] min-w-[240px] p-6 hover:shadow-lg hover:border-gray-400 transition-all duration-base"
      >
        <div
          aria-hidden
          className={cn(
            'absolute inset-0',
            board.pattern === 1 && 'kanban-pattern-1',
            board.pattern === 2 && 'kanban-pattern-2',
            board.pattern === 3 && 'kanban-pattern-3'
          )}
        />
        <div className="relative h-full flex flex-col justify-between">
          <div>
            <h3 className="display-serif text-2xl leading-tight mb-2">{board.name}</h3>
            {board.description && <p className="text-sm text-gray-600 line-clamp-2">{board.description}</p>}
          </div>
          <div className="space-y-1">
            <div className="text-xs text-gray-600">Создано: {formatDate(board.createdAt)}</div>
            <div className="text-xs text-gray-600">Автор: @{author?.username || '—'}</div>
            <div className="flex items-center gap-3 text-xs text-gray-600 mt-2">
              <span>{tasks.length} задач</span>
              <span className="text-gray-400">|</span>
              <span>{inProgress} в прогрессе</span>
            </div>
          </div>
        </div>
      </Link>
    </motion.div>
  )
}
