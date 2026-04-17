import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Modal } from '../ui/Modal'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'
import { useCreateBoard } from '../../hooks/useBoards'
import { toast } from 'sonner'

export function BoardCreateModal({ open, onClose, groupId }) {
  const createBoard = useCreateBoard()
  const nav = useNavigate()
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')

  const submit = (e) => {
    e.preventDefault()
    const trimmed = title.trim()
    if (trimmed.length < 3) return toast.error('Минимум 3 символа')
    createBoard.mutate(
      { groupId, title: trimmed, description: description.trim() || undefined },
      {
        onSuccess: (b) => {
          onClose()
          setTitle(''); setDescription('')
          if (b?.id) nav(`/boards/${b.id}`)
        },
      }
    )
  }

  return (
    <Modal open={open} onClose={onClose} title="Новая доска" size="sm">
      <form onSubmit={submit} className="px-6 py-5 space-y-4">
        <Input label="Название (3–100)" value={title} onChange={(e) => setTitle(e.target.value)} autoFocus />
        <Input label="Описание" value={description} onChange={(e) => setDescription(e.target.value)} />
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="ghost" type="button" onClick={onClose}>Отмена</Button>
          <Button type="submit" loading={createBoard.isPending}>Создать</Button>
        </div>
      </form>
    </Modal>
  )
}
