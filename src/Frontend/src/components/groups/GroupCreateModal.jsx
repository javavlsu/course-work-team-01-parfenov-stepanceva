import { useState } from 'react'
import { Modal } from '../ui/Modal'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'
import { useMockStore } from '../../store/mockStore'
import { toast } from 'sonner'

export function GroupCreateModal({ open, onClose, onCreated }) {
  const createGroup = useMockStore((s) => s.createGroup)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')

  const submit = (e) => {
    e.preventDefault()
    if (!name.trim()) return toast.error('Введите название')
    const g = createGroup({ name: name.trim(), description: description.trim() })
    toast.success('Группа создана')
    setName(''); setDescription('')
    onCreated?.(g)
    onClose()
  }

  return (
    <Modal open={open} onClose={onClose} title="Новая группа" size="sm">
      <form onSubmit={submit} className="px-6 py-5 space-y-4">
        <Input label="Название" value={name} onChange={(e) => setName(e.target.value)} autoFocus />
        <Input label="Описание (необязательно)" value={description} onChange={(e) => setDescription(e.target.value)} />
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="ghost" onClick={onClose} type="button">Отмена</Button>
          <Button type="submit">Создать</Button>
        </div>
      </form>
    </Modal>
  )
}
