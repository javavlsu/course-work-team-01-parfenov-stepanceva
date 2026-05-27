import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { invitationsApi } from '../api/resources'
import { groupsKey } from './useGroups'
import { t } from '../i18n'

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
      toast.success(t('groups.inviteSent'))
    },
    onError: (err) => {
      const status = err.response?.status
      // 404 — пользователь не найден; для 409/403 toast уже показал глобальный обработчик
      if (status === 404) toast.error(t('errors.US0001'))
      else if (status !== 409 && status !== 403) toast.error(t('groups.inviteFailed'))
    },
  })
}

export function useInviteByLink(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (expiresInDays) => invitationsApi.createLink(groupId, expiresInDays),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupInvitesKey(groupId) })
      toast.success(t('groups.linkCreated'))
    },
  })
}

export function useCancelInvitation(groupId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id) => invitationsApi.cancel(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: groupInvitesKey(groupId) })
      toast.success(t('groups.inviteRevoked'))
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
      toast.success(accept ? t('invitations.accepted') : t('invitations.declined'))
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
