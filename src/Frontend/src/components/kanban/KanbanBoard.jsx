import { useState, useMemo, useRef } from 'react'
import { DndContext, PointerSensor, KeyboardSensor, useSensor, useSensors, DragOverlay, closestCorners } from '@dnd-kit/core'
import { sortableKeyboardCoordinates, arrayMove } from '@dnd-kit/sortable'
import { motion, AnimatePresence } from 'framer-motion'
import { Plus, Check, X } from 'lucide-react'
import { Column } from './Column'
import { TaskCard } from './TaskCard'
import { useMockStore } from '../../store/mockStore'
import { toast } from 'sonner'

export function KanbanBoard({ boardId, onOpenTask }) {
  const columns = useMockStore((s) => s.listColumns(boardId))
  const tasks = useMockStore((s) => s.listTasks(boardId))
  const moveTask = useMockStore((s) => s.moveTask)
  const createColumn = useMockStore((s) => s.createColumn)

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 4 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  )

  const [activeTask, setActiveTask] = useState(null)
  const [addingCol, setAddingCol] = useState(false)
  const [colName, setColName] = useState('')

  const tasksByColumn = useMemo(() => {
    const map = {}
    columns.forEach((c) => (map[c.id] = []))
    tasks.forEach((t) => { if (map[t.columnId]) map[t.columnId].push(t) })
    return map
  }, [columns, tasks])

  const findContainer = (id) => {
    if (tasksByColumn[id]) return id
    const task = tasks.find((t) => t.id === id)
    return task?.columnId
  }

  const onDragStart = (e) => {
    const task = tasks.find((t) => t.id === e.active.id)
    setActiveTask(task || null)
  }

  const onDragEnd = (e) => {
    setActiveTask(null)
    const { active, over } = e
    if (!over) return
    if (active.id === over.id) return

    const fromCol = findContainer(active.id)
    const toCol = findContainer(over.id)
    if (!fromCol || !toCol) return

    const overIsTask = tasks.some((t) => t.id === over.id)
    let destIndex
    if (overIsTask) {
      const overTasks = tasksByColumn[toCol]
      destIndex = overTasks.findIndex((t) => t.id === over.id)
      if (destIndex === -1) destIndex = overTasks.length
    } else {
      destIndex = tasksByColumn[toCol]?.length || 0
    }
    moveTask(active.id, toCol, destIndex)
  }

  const submitCol = () => {
    if (!colName.trim()) { setAddingCol(false); return }
    createColumn(boardId, { name: colName.trim().toUpperCase() })
    toast.success('Колонка создана')
    setColName(''); setAddingCol(false)
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={onDragStart}
      onDragEnd={onDragEnd}
    >
      <div className="h-full scroll-x px-6 md:px-12 pb-6">
        <div className="flex gap-4 h-full items-stretch">
          {columns.map((c) => (
            <Column key={c.id} column={c} tasks={tasksByColumn[c.id] || []} onOpenTask={onOpenTask} />
          ))}

          <AnimatePresence mode="wait">
            {addingCol ? (
              <motion.div
                key="form"
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
                className="shrink-0 w-[300px] bg-gray-100 rounded-lg p-3 h-fit"
              >
                <input
                  autoFocus
                  value={colName}
                  onChange={(e) => setColName(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') submitCol()
                    if (e.key === 'Escape') { setColName(''); setAddingCol(false) }
                  }}
                  placeholder="Название колонки…"
                  className="w-full bg-paper border border-gray-200 rounded px-3 py-2 text-sm outline-none focus:border-ink"
                />
                <div className="flex gap-1 mt-2 justify-end">
                  <button onClick={() => { setAddingCol(false); setColName('') }} className="w-7 h-7 inline-flex items-center justify-center rounded hover:bg-gray-200"><X className="w-4 h-4" /></button>
                  <button onClick={submitCol} className="w-7 h-7 inline-flex items-center justify-center rounded bg-ink text-paper hover:bg-gray-800"><Check className="w-4 h-4" /></button>
                </div>
              </motion.div>
            ) : (
              <motion.button
                key="add"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                onClick={() => setAddingCol(true)}
                className="shrink-0 w-[300px] h-14 border border-dashed border-gray-200 hover:border-ink rounded-lg flex items-center justify-center gap-2 text-sm text-gray-600 hover:text-ink transition-colors"
              >
                <Plus className="w-4 h-4" /> Добавить колонку
              </motion.button>
            )}
          </AnimatePresence>
        </div>
      </div>

      <DragOverlay>
        {activeTask ? <TaskCard task={activeTask} isOverlay /> : null}
      </DragOverlay>
    </DndContext>
  )
}
