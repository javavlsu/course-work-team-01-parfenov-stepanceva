import { useState, useRef, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { Plus, X, Check, MoreHorizontal, Trash2, Pencil } from 'lucide-react'
import { TaskCard } from './TaskCard'
import { Dropdown } from '../ui/Dropdown'
import { Badge } from '../ui/Badge'
import { cn } from '../../utils/cn'
import { useCreateTask, useDeleteColumn, useUpdateColumn } from '../../hooks/useKanban'

export function Column({ boardId, column, tasks, usersById, onOpenTask }) {
  const { setNodeRef, isOver } = useDroppable({ id: column.id, data: { type: 'column', column } })
  const createTask = useCreateTask(boardId)
  const deleteColumn = useDeleteColumn(boardId)
  const updateColumn = useUpdateColumn(boardId)
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

  return (
    <div
      ref={setNodeRef}
      className={cn(
        'shrink-0 w-[300px] h-full bg-gray-100 rounded-lg flex flex-col transition-colors duration-base',
        isOver && 'bg-accent-2/40 ring-2 ring-ink/20'
      )}
    >
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200/60">
        {editingName ? (
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            onBlur={commitName}
            onKeyDown={(e) => { if (e.key === 'Enter') e.currentTarget.blur(); if (e.key === 'Escape') { setName(column.name); setEditingName(false) } }}
            className="bg-paper border border-gray-200 rounded px-2 py-1 text-sm mono uppercase tracking-[0.1em] outline-none focus:border-ink"
            autoFocus
          />
        ) : (
          <h3
            onDoubleClick={() => setEditingName(true)}
            className="mono text-sm tracking-[0.1em] uppercase text-gray-600 flex items-center gap-2"
          >
            {column.name}
            <Badge variant={tasks.length > 10 ? 'danger' : 'default'}>{tasks.length}</Badge>
          </h3>
        )}
        <Dropdown
          align="right"
          trigger={
            <button className="w-7 h-7 inline-flex items-center justify-center rounded hover:bg-gray-200 transition-colors" aria-label="More">
              <MoreHorizontal className="w-4 h-4" />
            </button>
          }
          items={[
            { label: 'Переименовать', icon: Pencil, onClick: () => setEditingName(true) },
            { label: 'Удалить колонку', icon: Trash2, danger: true, onClick: () => {
              if (confirm('Удалить колонку со всеми задачами?')) deleteColumn.mutate(column.id)
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
                  placeholder="Название задачи…"
                  className="w-full resize-none outline-none text-sm bg-transparent min-h-[60px]"
                />
                <div className="flex justify-end gap-1 mt-1">
                  <button onClick={() => { setAdding(false); setTitle('') }} className="w-7 h-7 inline-flex items-center justify-center rounded hover:bg-gray-100" aria-label="Отмена">
                    <X className="w-4 h-4" />
                  </button>
                  <button onClick={submit} className="w-7 h-7 inline-flex items-center justify-center rounded bg-ink text-paper hover:bg-gray-800" aria-label="Создать">
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
            <Plus className="w-4 h-4" /> Задача
          </button>
        )}
      </div>
    </div>
  )
}
