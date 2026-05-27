import { useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { formatDate } from '../../utils/dates'
import { useTranslation } from '../../i18n'
import { cn } from '../../utils/cn'

const DESC_CLAMP_THRESHOLD = 120

export function BoardCard({ board, index = 0 }) {
  const [descExpanded, setDescExpanded] = useState(false)
  const { t } = useTranslation()
  const isLongDesc = board.description && board.description.length > DESC_CLAMP_THRESHOLD

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: index * 0.06, ease: [0.16, 1, 0.3, 1] }}
      whileHover={{ y: -3 }}
    >
      <Link
        to={`/boards/${board.id}`}
        className="group relative block overflow-hidden bg-paper border border-gray-100 rounded-lg min-w-[240px] p-6 hover:shadow-lg hover:border-gray-400 transition-all duration-base"
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
        <div className="relative flex flex-col gap-4">
          <div>
            <h3 className="display-serif text-2xl leading-tight mb-2">{board.name}</h3>
            {board.description && (
              <div>
                <p className={cn('text-sm text-gray-600 break-words', !descExpanded && 'line-clamp-3')}>
                  {board.description}
                </p>
                {isLongDesc && (
                  <button
                    onClick={(e) => { e.preventDefault(); e.stopPropagation(); setDescExpanded((v) => !v) }}
                    className="text-xs text-gray-400 hover:text-ink mt-1 transition-colors"
                  >
                    {descExpanded ? t('common.close') : t('common.more')}
                  </button>
                )}
              </div>
            )}
          </div>
          <div className="space-y-1">
            <div className="text-xs text-gray-600">{t('tasks.createdAt')}: {formatDate(board.createdAt)}</div>
            {board.author?.username && <div className="text-xs text-gray-600">@{board.author.username}</div>}
          </div>
        </div>
      </Link>
    </motion.div>
  )
}
