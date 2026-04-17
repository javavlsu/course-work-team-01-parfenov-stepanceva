import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { columnsApi, tasksApi } from '../api/resources'
import { taskHistoryKey } from './useTaskHistory'

export const columnsKey = (boardId) => ['columns', boardId]
export const tasksKey = (boardId) => ['tasks', boardId]

export function useColumns(boardId) {
  return useQuery({
    queryKey: columnsKey(boardId),
    queryFn: () => columnsApi.list(boardId),
    enabled: !!boardId,
  })
}

export function useTasks(boardId) {
  return useQuery({
    queryKey: tasksKey(boardId),
    queryFn: () => tasksApi.list(boardId),
    enabled: !!boardId,
  })
}

export function useCreateColumn(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (title) => columnsApi.create(boardId, title),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: columnsKey(boardId) })
      toast.success('Колонка создана')
    },
  })
}

export function useUpdateColumn(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ columnId, title, position }) =>
      columnsApi.update(boardId, columnId, { title, position }),
    onSuccess: () => qc.invalidateQueries({ queryKey: columnsKey(boardId) }),
  })
}

export function useMoveColumn(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ columnId, newPosition }) => columnsApi.move(boardId, columnId, newPosition),
    onSuccess: () => qc.invalidateQueries({ queryKey: columnsKey(boardId) }),
  })
}

export function useDeleteColumn(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (columnId) => columnsApi.remove(boardId, columnId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: columnsKey(boardId) })
      qc.invalidateQueries({ queryKey: tasksKey(boardId) })
      toast.success('Колонка удалена')
    },
  })
}

export function useCreateTask(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data) => tasksApi.create(boardId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: tasksKey(boardId) }),
  })
}

export function useUpdateTask(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ taskId, data }) => tasksApi.update(boardId, taskId, data),
    onSuccess: (_, { taskId }) => {
      qc.invalidateQueries({ queryKey: tasksKey(boardId) })
      qc.invalidateQueries({ queryKey: taskHistoryKey(boardId, taskId) })
    },
  })
}

export function useDeleteTask(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (taskId) => tasksApi.remove(boardId, taskId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: tasksKey(boardId) })
      toast.success('Задача удалена')
    },
  })
}

// Optimistic move: update cache immediately, then call API.
export function useMoveTask(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ taskId, columnId, position }) =>
      tasksApi.update(boardId, taskId, { columnId, position }),
    onMutate: async ({ taskId, columnId, position }) => {
      await qc.cancelQueries({ queryKey: tasksKey(boardId) })
      const prev = qc.getQueryData(tasksKey(boardId))
      if (!prev) return { prev }
      const tasks = [...prev]
      const task = tasks.find((t) => t.id === taskId)
      if (!task) return { prev }
      const oldCol = task.columnId
      const fromSame = oldCol === columnId

      // Remove from source column and renumber
      const sourceCol = tasks.filter((t) => t.columnId === oldCol && t.id !== taskId).sort((a, b) => a.order - b.order)
      sourceCol.forEach((t, i) => { t.order = i })

      // Insert into destination
      const destCol = tasks.filter((t) => t.columnId === columnId && t.id !== taskId).sort((a, b) => a.order - b.order)
      const clamped = Math.max(0, Math.min(position, destCol.length))
      destCol.splice(clamped, 0, task)
      task.columnId = columnId
      destCol.forEach((t, i) => { t.order = i })

      qc.setQueryData(tasksKey(boardId), tasks)
      return { prev }
    },
    onError: (_err, _vars, ctx) => {
      if (ctx?.prev) qc.setQueryData(tasksKey(boardId), ctx.prev)
      toast.error('Не удалось переместить задачу')
    },
    onSettled: () => qc.invalidateQueries({ queryKey: tasksKey(boardId) }),
  })
}
