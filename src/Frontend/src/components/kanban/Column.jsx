import { useState, useRef, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy, useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { Plus, X, Check, MoreHorizontal, Trash2, Pencil, GripVertical } from 'lucide-react'
import { TaskCard } from './TaskCard'
import { Dropdown } from '../ui/Dropdown'
import { Badge } from '../ui/Badge'
import { cn } from '../../utils/cn'
import { useCreateTask, useDeleteColumn, useUpdateColumn } from '../../hooks/useKanban'
import { useTranslation } from '../../i18n'

export function Column({ boardId, column, tasks, usersById, onOpenTask }) {
  const {
    attributes,
    listeners,
    setNodeRef: setSortableRef,
    transform,
    transition,
    isDragging: isColDragging,
  } = useSortable({ id: `col-${column.id}`, data: { type: 'column', column } })

  const { setNodeRef: setDroppableRef, isOver } = useDroppable({ id: column.id, data: { type: 'column', column } })

  const setNodeRef = useCallback(
    (node) => { setSortableRef(node); setDroppableRef(node) },
    [setSortableRef, setDroppableRef]
  )
  const createTask = useCreateTask(boardId)
  const deleteColumn = useDeleteColumn(boardId)
  const updateColumn = useUpdateColumn(boardId)
  const { t } = useTranslation()
  const [adding, setAdding] = useState(false)
  const [title, setTitle] = useState('')
  const [editingName, setEditingName] = useState(false)
  const [name, setName] = useState(column.name)
  const inputRef = useRef(null)

  useEffect(() => { if (adding) inputRef.current?.focus() }, [adding])
  useEffect(() => { setName(column.name) }, [column.name])

  const submit = () => {
    if (!title.trim()) { setAdding(false); return }
    createTask.mutate(
      { columnId: column.id, title: title.trim(), priority: 'MEDIUM' },
      {
        onSuccess: () => { setTitle(''); setAdding(false) },
      }
    )
  }

  const onKey = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); submit() }
    if (e.key === 'Escape') { setTitle(''); setAdding(false) }
  }

  const commitName = () => {
    const trimmed = name.trim()
    if (trimmed && trimmed !== column.name) {
      updateColumn.mutate({ columnId: column.id, title: trimmed })
    }
    setEditingName(false)
  }

  const colStyle = { transform: CSS.Transform.toString(transform), transition }

  return (
    <div
      ref={setNodeRef}
      style={colStyle}
      {...attributes}
      className={cn(
        'shrink-0 w-[300px] h-full bg-gray-100 rounded-lg flex flex-col transition-colors duration-base',
        isOver && 'bg-accent-2/40 ring-2 ring-ink/20',
        isColDragging && 'opacity-40'
      )}
    >
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200/60">
        <div
          {...listeners}
          className="w-5 h-5 flex items-center justify-center text-gray-300 hover:text-gray-500 cursor-grab active:cursor-grabbing shrink-0 mr-1"
          aria-label={t('common.more')}
        >
          <GripVertical className="w-4 h-4" />
        </div>
        {editingName ? (
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            onBlur={commitName}
            onKeyDown={(e) => { if (e.key === 'Enter') e.currentTarget.blur(); if (e.key === 'Escape') { setName(column.name); setEditingName(false) } }}
            className="flex-1 bg-paper border border-gray-200 rounded px-2 py-1 text-sm mono uppercase tracking-[0.1em] outline-none focus:border-ink"
            autoFocus
          />
        ) : (
          <h3
            onDoubleClick={() => setEditingName(true)}
            className="flex-1 mono text-sm tracking-[0.1em] uppercase text-gray-600 flex items-center gap-2"
          >
            {column.name}
            <Badge variant={tasks.length > 10 ? 'danger' : 'default'}>{tasks.length}</Badge>
          </h3>
        )}
        <Dropdown
          align="right"
          trigger={
            <button className="w-7 h-7 inline-flex items-center justify-center rounded hover:bg-gray-200 transition-colors" aria-label={t('common.actions')}>
              <MoreHorizontal className="w-4 h-4" />
            </button>
          }
          items={[
            { label: t('common.rename'), icon: Pencil, onClick: () => setEditingName(true) },
            { label: t('common.delete'), icon: Trash2, danger: true, onClick: () => {
              if (confirm(t('columns.deleteConfirm'))) deleteColumn.mutate(column.id)
            } },
          ]}
        />
      </div>

      <div className="flex-1 overflow-y-auto p-3 space-y-2">
        <SortableContext items={tasks.map((t) => t.id)} strategy={verticalListSortingStrategy}>
          <AnimatePresence initial={false}>
            {tasks.map((t) => (
              <TaskCard key={t.id} task={t} usersById={usersById} onOpen={onOpenTask} />
            ))}
          </AnimatePresence>
        </SortableContext>

        <AnimatePresence initial={false}>
          {adding && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.25 }}
              className="overflow-hidden"
            >
              <div className="bg-paper rounded-md p-3 border border-gray-200">
                <textarea
                  ref={inputRef}
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  onKeyDown={onKey}
                  placeholder={t('tasks.titlePlaceholder')}
                  className="w-full resize-none outline-none text-sm bg-transparent min-h-[60px]"
                />
                <div className="flex justify-end gap-1 mt-1">
                  <button onClick={() => { setAdding(false); setTitle('') }} className="w-7 h-7 inline-flex items-center justify-center rounded hover:bg-gray-100" aria-label={t('common.cancel')}>
                    <X className="w-4 h-4" />
                  </button>
                  <button onClick={submit} className="w-7 h-7 inline-flex items-center justify-center rounded bg-ink text-paper hover:bg-gray-800" aria-label={t('common.create')}>
                    <Check className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {!adding && (
          <button
            onClick={() => setAdding(true)}
            className="w-full flex items-center gap-2 px-2 py-2 text-sm text-gray-600 hover:text-ink hover:bg-gray-200/60 rounded-md transition-colors"
          >
            <Plus className="w-4 h-4" /> {t('tasks.addTask')}
          </button>
        )}
      </div>
    </div>
  )
}
