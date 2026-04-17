import { useState, useMemo } from 'react'
import { DndContext, PointerSensor, KeyboardSensor, useSensor, useSensors, DragOverlay, closestCorners } from '@dnd-kit/core'
import { SortableContext, sortableKeyboardCoordinates, horizontalListSortingStrategy } from '@dnd-kit/sortable'
import { motion, AnimatePresence } from 'framer-motion'
import { Plus, Check, X } from 'lucide-react'
import { Column } from './Column'
import { TaskCard } from './TaskCard'
import { useColumns, useTasks, useCreateColumn, useMoveTask, useMoveColumn } from '../../hooks/useKanban'
import { useBoardUsers } from '../../hooks/useBoards'

export function KanbanBoard({ boardId, onOpenTask }) {
  const { data: columns = [], isLoading: colLoading } = useColumns(boardId)
  const { data: tasks = [], isLoading: taskLoading } = useTasks(boardId)
  const { data: boardUsers = [] } = useBoardUsers(boardId)
  const createColumn = useCreateColumn(boardId)
  const moveTask = useMoveTask(boardId)
  const moveColumn = useMoveColumn(boardId)

  const usersById = useMemo(() => Object.fromEntries(boardUsers.map((u) => [u.id, u])), [boardUsers])

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 4 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  )

  const [activeTask, setActiveTask] = useState(null)
  const [activeColumn, setActiveColumn] = useState(null)
  const [addingCol, setAddingCol] = useState(false)
  const [colName, setColName] = useState('')

  const sortedColumns = useMemo(() => [...columns].sort((a, b) => a.order - b.order), [columns])
  const sortedTasks = useMemo(() => [...tasks].sort((a, b) => a.order - b.order), [tasks])

  const tasksByColumn = useMemo(() => {
    const map = {}
    sortedColumns.forEach((c) => (map[c.id] = []))
    sortedTasks.forEach((t) => { if (map[t.columnId]) map[t.columnId].push(t) })
    return map
  }, [sortedColumns, sortedTasks])

  const colIds = useMemo(() => sortedColumns.map((c) => `col-${c.id}`), [sortedColumns])

  const findContainer = (id) => {
    const strId = String(id)
    if (strId.startsWith('col-')) {
      const numId = Number(strId.slice(4))
      return tasksByColumn[numId] !== undefined ? numId : null
    }
    if (tasksByColumn[id] !== undefined) return id
    const task = sortedTasks.find((t) => t.id === id)
    return task?.columnId ?? null
  }

  const onDragStart = (e) => {
    if (e.active.data.current?.type === 'column') {
      setActiveColumn(sortedColumns.find((c) => `col-${c.id}` === e.active.id) || null)
      return
    }
    const task = sortedTasks.find((t) => t.id === e.active.id)
    setActiveTask(task || null)
  }

  const onDragEnd = (e) => {
    const { active, over } = e
    setActiveTask(null)
    setActiveColumn(null)
    if (!over || active.id === over.id) return

    // Column reorder
    if (active.data.current?.type === 'column') {
      const fromIdx = sortedColumns.findIndex((c) => `col-${c.id}` === active.id)
      const toIdx = sortedColumns.findIndex((c) => `col-${c.id}` === over.id)
      if (fromIdx !== -1 && toIdx !== -1 && fromIdx !== toIdx) {
        moveColumn.mutate({ columnId: sortedColumns[fromIdx].id, newPosition: toIdx + 1 })
      }
      return
    }

    // Task move
    const fromCol = findContainer(active.id)
    const toCol = findContainer(over.id)
    if (!fromCol || !toCol) return

    const overIsTask = sortedTasks.some((t) => t.id === over.id)
    let destIndex
    if (overIsTask) {
      const overTasks = tasksByColumn[toCol]
      destIndex = overTasks.findIndex((t) => t.id === over.id)
      if (destIndex === -1) destIndex = overTasks.length
    } else {
      destIndex = tasksByColumn[toCol]?.length || 0
    }
    moveTask.mutate({ taskId: active.id, columnId: toCol, position: destIndex })
  }

  const submitCol = () => {
    const trimmed = colName.trim()
    if (!trimmed) { setAddingCol(false); return }
    createColumn.mutate(trimmed.toUpperCase(), {
      onSuccess: () => { setColName(''); setAddingCol(false) },
    })
  }

  if (colLoading || taskLoading) {
    return (
      <div className="h-full scroll-x px-6 md:px-12 pb-6">
        <div className="flex gap-4 h-full items-stretch">
          {[0, 1, 2].map((i) => <div key={i} className="shrink-0 w-[300px] skeleton rounded-lg" />)}
        </div>
      </div>
    )
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
          <SortableContext items={colIds} strategy={horizontalListSortingStrategy}>
            {sortedColumns.map((c) => (
              <Column
                key={c.id}
                boardId={boardId}
                column={c}
                tasks={tasksByColumn[c.id] || []}
                usersById={usersById}
                onOpenTask={onOpenTask}
              />
            ))}
          </SortableContext>

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
        {activeColumn ? (
          <div className="shrink-0 w-[300px] bg-gray-100 rounded-lg opacity-90 shadow-xl ring-2 ring-ink/20 h-20 flex items-center px-4">
            <span className="mono text-sm tracking-[0.1em] uppercase text-gray-600">{activeColumn.name}</span>
          </div>
        ) : activeTask ? (
          <TaskCard task={activeTask} usersById={usersById} isOverlay />
        ) : null}
      </DragOverlay>
    </DndContext>
  )
}
