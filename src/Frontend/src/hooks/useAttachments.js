import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { attachmentsApi } from '../api/resources'
import { translateError, t } from '../i18n'

export const attachmentsKey = (boardId, taskId) => ['attachments', boardId, taskId]

export function useAttachments(boardId, taskId) {
  return useQuery({
    queryKey: attachmentsKey(boardId, taskId),
    queryFn: () => attachmentsApi.list(boardId, taskId),
    enabled: !!boardId && !!taskId,
  })
}

export function useUploadAttachment(boardId, taskId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (file) => attachmentsApi.upload(boardId, taskId, file),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: attachmentsKey(boardId, taskId) })
      toast.success(t('tasks.fileAttached'))
    },
    onError: (err) => toast.error(translateError(err, 'tasks.fileUploadFailed')),
  })
}

export function useDeleteAttachment(boardId, taskId) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (attachmentId) => attachmentsApi.remove(boardId, taskId, attachmentId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: attachmentsKey(boardId, taskId) })
      toast.success(t('tasks.fileRemoved'))
    },
    onError: (err) => toast.error(translateError(err, 'tasks.fileRemoveFailed')),
  })
}
