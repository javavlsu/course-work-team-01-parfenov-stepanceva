import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { boardsApi, boardUsersApi } from '../api/resources'

export const boardsAllKey = ['boards', 'all']
export const boardsForGroupKey = (groupId) => ['boards', 'group', groupId]
export const boardKey = (id) => ['board', id]
export const boardUsersKey = (boardId) => ['board', boardId, 'users']

export function useMyBoards() {
  return useQuery({ queryKey: boardsAllKey, queryFn: boardsApi.all })
}

export function useGroupBoards(groupId) {
  return useQuery({
    queryKey: boardsForGroupKey(groupId),
    queryFn: () => boardsApi.listForGroup(groupId),
    enabled: !!groupId,
  })
}

export function useBoard(id) {
  return useQuery({ queryKey: boardKey(id), queryFn: () => boardsApi.get(id), enabled: !!id })
}

export function useCreateBoard() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ groupId, title, description }) => boardsApi.create(groupId, title, description),
    onSuccess: (b) => {
      qc.invalidateQueries({ queryKey: boardsAllKey })
      if (b?.groupId) qc.invalidateQueries({ queryKey: boardsForGroupKey(b.groupId) })
      toast.success('Доска создана')
    },
  })
}

export function useUpdateBoard() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, title, description }) => boardsApi.update(id, title, description),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: boardsAllKey })
      qc.invalidateQueries({ queryKey: boardKey(id) })
      toast.success('Сохранено')
    },
  })
}

export function useDeleteBoard() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id) => boardsApi.remove(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: boardsAllKey })
      toast.success('Доска удалена')
    },
  })
}

export function useBoardUsers(boardId) {
  return useQuery({
    queryKey: boardUsersKey(boardId),
    queryFn: () => boardUsersApi.list(boardId),
    enabled: !!boardId,
  })
}

export function useAddBoardUser(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (userId) => boardUsersApi.add(boardId, userId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: boardUsersKey(boardId) })
      toast.success('Участник добавлен на доску')
    },
    onError: (err) => {
      const msg = err.response?.data?.message
      toast.error(msg || 'Не удалось добавить участника')
    },
  })
}

export function useRemoveBoardUser(boardId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (userId) => boardUsersApi.remove(boardId, userId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: boardUsersKey(boardId) })
      toast.success('Участник удалён с доски')
    },
    onError: (err) => {
      const msg = err.response?.data?.message
      toast.error(msg || 'Не удалось удалить участника')
    },
  })
}
