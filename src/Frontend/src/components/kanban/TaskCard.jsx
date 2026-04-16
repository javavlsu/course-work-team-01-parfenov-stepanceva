import { forwardRef, memo } from 'react'
import { motion } from 'framer-motion'
import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { MessageSquare, Paperclip, Pencil, AlertCircle } from 'lucide-react'
import { Avatar } from '../ui/Avatar'
import { Dot } from '../ui/Badge'
import { useMockStore } from '../../store/mockStore'
import { PRIORITIES } from '../../utils/priorities'
import { deadlineStatus } from '../../utils/dates'
import { cn } from '../../utils/cn'

function TaskCardImpl({ task, onOpen, isOverlay = false }) {
  const users = useMockStore((s) => s.users)
  const assignee = task.assigneeId ? users[task.assigneeId] : null
  const p = PRIORITIES[task.priority] || PRIORITIES.MEDIUM
  const dl = deadlineStatus(task.deadline)

  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: task.id,
    data: { type: 'task', task },
  })

  const style = isOverlay
    ? undefined
    : { transform: CSS.Transform.toString(transform), transition }

  return (
    <motion.div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      onClick={(e) => { if (!isDragging) onOpen?.(task) }}
      initial={!isOverlay ? { opacity: 0, y: 8 } : false}
      animate={!isOverlay ? { opacity: 1, y: 0 } : false}
      whileHover={!isOverlay && !isDragging ? { y: -2 } : undefined}
      transition={{ duration: 0.25, ease: [0.16, 1, 0.3, 1] }}
      className={cn(
        'group relative bg-paper rounded-md p-3.5 border border-gray-100 text-left cursor-grab active:cursor-grabbing select-none',
        'hover:shadow-md transition-shadow duration-base',
        isDragging && !isOverlay && 'opacity-40 border-dashed',
        isOverlay && 'shadow-xl rotate-2 scale-[1.04]'
      )}
    >
      <div className="flex items-center justify-between mb-2 text-[11px] mono">
        <div className="inline-flex items-center gap-1.5">
          <Dot color={p.color} />
          <span className="tracking-[0.08em] uppercase text-gray-600">{p.label}</span>
        </div>
        {task.deadline && (
          <span
            className={cn(
              'inline-flex items-center gap-1',
              dl.color === 'danger' && 'text-priority-high',
              dl.color === 'warning' && 'text-priority-medium',
              dl.color === 'neutral' && 'text-gray-400'
            )}
          >
            {dl.color === 'danger' && <AlertCircle className="w-3 h-3" />}
            {dl.label}
          </span>
        )}
      </div>

      <div className="text-sm text-ink leading-snug mb-1 line-clamp-2">{task.title}</div>

      {task.description && (
        <div className="text-xs text-gray-600 line-clamp-2 mb-3">{task.description}</div>
      )}

      <div className="flex items-center justify-between mt-2">
        <div className="flex items-center gap-3">
          {assignee ? <Avatar user={assignee} size="xs" /> : <div className="w-6 h-6 rounded-full border border-dashed border-gray-200" />}
          {task.commentsCount > 0 && (
            <span className="inline-flex items-center gap-1 text-[11px] text-gray-400">
              <MessageSquare className="w-3 h-3" /> {task.commentsCount}
            </span>
          )}
          {task.attachmentsCount > 0 && (
            <span className="inline-flex items-center gap-1 text-[11px] text-gray-400">
              <Paperclip className="w-3 h-3" /> {task.attachmentsCount}
            </span>
          )}
        </div>
        <Pencil className="w-3.5 h-3.5 text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity" />
      </div>
    </motion.div>
  )
}

export const TaskCard = memo(TaskCardImpl, (a, b) =>
  a.task.id === b.task.id &&
  a.task.title === b.task.title &&
  a.task.priority === b.task.priority &&
  a.task.deadline === b.task.deadline &&
  a.task.assigneeId === b.task.assigneeId &&
  a.task.description === b.task.description &&
  a.task.commentsCount === b.task.commentsCount &&
  a.task.attachmentsCount === b.task.attachmentsCount &&
  a.isOverlay === b.isOverlay
)
