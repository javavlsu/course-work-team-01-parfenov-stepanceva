import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { invitationsApi } from '../api/resources'
import { groupsKey, membersKey } from './useGroups'

export const myInvitesKey = ['invitations', 'my']
export const groupInvitesKey = (groupId) => ['invitations', 'group', groupId]

export function useMyInvitations() {
  return useQuery({ queryKey: myInvitesKey, queryFn: invitationsApi.my })
}

export function useGroupInvitations(groupId) {
  return useQuery({
    queryKey: groupInvitesKey(groupId),
    queryFn: () => invitationsApi.listForGroup(groupId),
    enabled: !!groupId,
  })
}

export function useInviteByEmail(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ email, expiresInDays }) => invitationsApi.createEmail(groupId, email, expiresInDays),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupInvitesKey(groupId) })
      toast.success('Приглашение отправлено')
    },
    onError: (err) => {
      const status = err.response?.status
      if (status === 404) toast.error('Пользователь не найден')
      else if (status !== 409 && status !== 403) toast.error('Не удалось отправить')
    },
  })
}

export function useInviteByLink(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (expiresInDays) => invitationsApi.createLink(groupId, expiresInDays),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupInvitesKey(groupId) })
      toast.success('Ссылка создана')
    },
  })
}

export function useCancelInvitation(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id) => invitationsApi.cancel(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupInvitesKey(groupId) })
      toast.success('Отозвано')
    },
  })
}

export function useRespondInvitation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, accept }) => invitationsApi.respond(id, accept),
    onSuccess: (_, { accept }) => {
      qc.invalidateQueries({ queryKey: myInvitesKey })
      qc.invalidateQueries({ queryKey: groupsKey })
      toast.success(accept ? 'Приглашение принято' : 'Приглашение отклонено')
    },
  })
}

export function useJoinByToken() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (token) => invitationsApi.join(token),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupsKey })
    },
  })
}
