import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Modal } from '../ui/Modal'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'
import { useMockStore } from '../../store/mockStore'
import { toast } from 'sonner'

export function BoardCreateModal({ open, onClose, groupId }) {
  const createBoard = useMockStore((s) => s.createBoard)
  const nav = useNavigate()
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')

  const submit = (e) => {
    e.preventDefault()
    if (!name.trim()) return toast.error('Введите название')
    const b = createBoard(groupId, { name: name.trim(), description: description.trim() })
    toast.success('Доска создана')
    onClose()
    setName(''); setDescription('')
    nav(`/boards/${b.id}`)
  }

  return (
    <Modal open={open} onClose={onClose} title="Новая доска" size="sm">
      <form onSubmit={submit} className="px-6 py-5 space-y-4">
        <Input label="Название" value={name} onChange={(e) => setName(e.target.value)} autoFocus />
        <Input label="Описание" value={description} onChange={(e) => setDescription(e.target.value)} />
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="ghost" type="button" onClick={onClose}>Отмена</Button>
          <Button type="submit">Создать</Button>
        </div>
      </form>
    </Modal>
  )
}
