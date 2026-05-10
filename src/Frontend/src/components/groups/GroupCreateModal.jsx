import { useState } from 'react'
import { Modal } from '../ui/Modal'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'
import { useCreateGroup } from '../../hooks/useGroups'
import { useTranslation } from '../../i18n'
import { toast } from 'sonner'

export function GroupCreateModal({ open, onClose, onCreated }) {
  const createGroup = useCreateGroup()
  const { t } = useTranslation()
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')

  const submit = (e) => {
    e.preventDefault()
    const trimmed = name.trim()
    if (trimmed.length < 3) return toast.error(t('groups.minMembers'))
    createGroup.mutate({ name: trimmed, description: description.trim() || undefined }, {
      onSuccess: (g) => {
        setName(''); setDescription('')
        onCreated?.(g)
        onClose()
      },
    })
  }

  return (
    <Modal open={open} onClose={onClose} title={t('groups.createTitle')} size="sm">
      <form onSubmit={submit} className="px-6 py-5 space-y-4">
        <Input label={t('groups.nameLabel')} value={name} onChange={(e) => setName(e.target.value)} autoFocus />
        <Input label={`${t('groups.descLabel')} (${t('common.optional')})`} value={description} onChange={(e) => setDescription(e.target.value)} />
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="ghost" onClick={onClose} type="button">{t('common.cancel')}</Button>
          <Button type="submit" loading={createGroup.isPending}>{t('common.create')}</Button>
        </div>
      </form>
    </Modal>
  )
}
