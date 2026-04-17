import { useQuery } from '@tanstack/react-query'
import { taskHistoryApi } from '../api/resources'

export const taskHistoryKey = (boardId, taskId) => ['taskHistory', boardId, taskId]

export function useTaskHistory(boardId, taskId) {
  return useQuery({
    queryKey: taskHistoryKey(boardId, taskId),
    queryFn: () => taskHistoryApi.list(boardId, taskId),
    enabled: !!boardId && !!taskId,
  })
}
