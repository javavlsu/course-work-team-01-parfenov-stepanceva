import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { commentsApi } from '../api/resources'

export const commentsKey = (boardId, taskId) => ['comments', boardId, taskId]

export function useComments(boardId, taskId) {
  return useQuery({
    queryKey: commentsKey(boardId, taskId),
    queryFn: () => commentsApi.list(boardId, taskId),
    enabled: !!boardId && !!taskId,
  })
}

export function useCreateComment(boardId, taskId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (text) => commentsApi.create(boardId, taskId, text),
    onSuccess: () => qc.invalidateQueries({ queryKey: commentsKey(boardId, taskId) }),
  })
}

export function useDeleteComment(boardId, taskId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (commentId) => commentsApi.remove(boardId, taskId, commentId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: commentsKey(boardId, taskId) })
      toast.success('Удалено')
    },
  })
}
