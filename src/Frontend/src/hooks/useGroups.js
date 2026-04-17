import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { groupsApi, membersApi } from '../api/resources'

export const groupsKey = ['groups']
export const groupKey = (id) => ['group', id]
export const membersKey = (groupId) => ['group', groupId, 'members']

export function useGroups() {
  return useQuery({ queryKey: groupsKey, queryFn: groupsApi.list })
}

export function useGroup(id) {
  return useQuery({ queryKey: groupKey(id), queryFn: () => groupsApi.get(id), enabled: !!id })
}

export function useGroupMembers(groupId) {
  return useQuery({ queryKey: membersKey(groupId), queryFn: () => membersApi.list(groupId), enabled: !!groupId })
}

export function useCreateGroup() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data) => groupsApi.create(data),
    onSuccess: (g) => {
      qc.invalidateQueries({ queryKey: groupsKey })
      toast.success('Группа создана')
      return g
    },
  })
}

export function useUpdateGroup() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }) => groupsApi.update(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: groupsKey })
      qc.invalidateQueries({ queryKey: groupKey(id) })
      toast.success('Обновлено')
    },
  })
}

export function useDeleteGroup() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id) => groupsApi.remove(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupsKey })
      toast.success('Группа удалена')
    },
  })
}

export function useUpdateMemberRole(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ userId, role }) => membersApi.updateRole(groupId, userId, role),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: membersKey(groupId) })
      toast.success('Роль обновлена')
    },
  })
}

export function useRemoveMember(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (userId) => membersApi.remove(groupId, userId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: membersKey(groupId) })
      toast.success('Участник удалён')
    },
  })
}
