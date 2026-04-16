import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Trash2, Clock, User as UserIcon, Send } from 'lucide-react'
import { Modal } from '../ui/Modal'
import { Button } from '../ui/Button'
import { Avatar } from '../ui/Avatar'
import { Dot } from '../ui/Badge'
import { useMockStore } from '../../store/mockStore'
import { PRIORITIES, STATUSES } from '../../utils/priorities'
import { formatDate, formatRelative } from '../../utils/dates'
import { cn } from '../../utils/cn'
import { toast } from 'sonner'

export function TaskModal({ taskId, open, onClose }) {
  const task = useMockStore((s) => (taskId ? s.tasks[taskId] : null))
  const updateTask = useMockStore((s) => s.updateTask)
  const removeTask = useMockStore((s) => s.removeTask)
  const columns = useMockStore((s) => {
    if (!task) return []
    return Object.values(s.columns).filter((c) => c.boardId === s.columns[task.columnId]?.boardId).sort((a, b) => a.order - b.order)
  })
  const users = useMockStore((s) => s.users)
  const comments = useMockStore((s) => (taskId ? s.listComments(taskId) : []))
  const addComment = useMockStore((s) => s.addComment)
  const removeComment = useMockStore((s) => s.removeComment)
  const currentUser = useMockStore((s) => s.currentUser)

  const [title, setTitle] = useState('')
  const [editingTitle, setEditingTitle] = useState(false)
  const [desc, setDesc] = useState('')
  const [editingDesc, setEditingDesc] = useState(false)
  const [comment, setComment] = useState('')

  useEffect(() => {
    if (task) { setTitle(task.title); setDesc(task.description || '') }
  }, [taskId])

  if (!task) return null

  const saveTitle = () => {
    if (title.trim() && title !== task.title) {
      updateTask(task.id, { title: title.trim() })
      toast.success('Сохранено')
    }
    setEditingTitle(false)
  }
  const saveDesc = () => {
    updateTask(task.id, { description: desc })
    setEditingDesc(false)
  }

  const sendComment = () => {
    if (!comment.trim()) return
    addComment(task.id, comment.trim())
    setComment('')
  }

  const assignee = task.assigneeId ? users[task.assigneeId] : null
  const p = PRIORITIES[task.priority]

  return (
    <Modal open={open} onClose={onClose} size="lg" hideClose>
      <div className="px-6 pt-5 pb-4 flex items-center justify-between border-b border-gray-100">
        <div className="mono text-xs tracking-[0.1em] uppercase text-gray-400">Задача</div>
        <div className="flex items-center gap-1">
          <button
            onClick={() => { if (confirm('Удалить задачу?')) { removeTask(task.id); toast.success('Удалена'); onClose() } }}
            className="w-8 h-8 inline-flex items-center justify-center rounded-md text-gray-400 hover:text-priority-high hover:bg-priority-high/10"
            aria-label="Удалить"
          >
            <Trash2 className="w-4 h-4" />
          </button>
          <button onClick={onClose} className="w-8 h-8 inline-flex items-center justify-center rounded-md hover:bg-gray-100" aria-label="Закрыть">
            ✕
          </button>
        </div>
      </div>

      <div className="grid md:grid-cols-[1fr_240px] gap-0 max-h-[78vh] overflow-y-auto">
        <div className="p-6 space-y-6 border-r border-gray-100">
          <div>
            {editingTitle ? (
              <textarea
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                onBlur={saveTitle}
                onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); saveTitle() } }}
                autoFocus
                className="w-full display-serif text-2xl outline-none bg-transparent border-b border-gray-200 focus:border-ink pb-1 resize-none"
                rows={2}
              />
            ) : (
              <h2 onClick={() => setEditingTitle(true)} className="display-serif text-2xl cursor-text">{task.title}</h2>
            )}
          </div>

          <div>
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-2">Описание</div>
            {editingDesc ? (
              <textarea
                value={desc}
                onChange={(e) => setDesc(e.target.value)}
                onBlur={saveDesc}
                autoFocus
                rows={5}
                className="w-full text-sm bg-paper border border-gray-200 rounded-md p-3 outline-none focus:border-ink resize-y"
                placeholder="Добавьте описание…"
              />
            ) : (
              <div
                onClick={() => setEditingDesc(true)}
                className="cursor-text text-sm leading-relaxed min-h-[60px] p-3 rounded-md hover:bg-gray-100/50 whitespace-pre-wrap"
              >
                {task.description || <span className="text-gray-400">Добавьте описание…</span>}
              </div>
            )}
          </div>

          <div>
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-3">Комментарии</div>
            <div className="space-y-4 mb-4">
              {comments.length === 0 && <div className="text-sm text-gray-400">Пока нет комментариев</div>}
              {comments.map((c) => {
                const au = users[c.authorId]
                const mine = c.authorId === currentUser.id
                return (
                  <div key={c.id} className="flex gap-3 group/comment">
                    <Avatar user={au} size="sm" />
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-sm font-medium">{au?.username}</span>
                        <span className="text-xs text-gray-400" title={formatDate(c.createdAt, 'dd MMM yyyy, HH:mm')}>{formatRelative(c.createdAt)}</span>
                        {mine && (
                          <button
                            onClick={() => removeComment(c.id)}
                            className="ml-auto text-xs text-gray-400 hover:text-priority-high opacity-0 group-hover/comment:opacity-100 transition-opacity"
                          >Удалить</button>
                        )}
                      </div>
                      <div className="text-sm text-gray-800 whitespace-pre-wrap">{c.text}</div>
                    </div>
                  </div>
                )
              })}
            </div>
            <div className="flex gap-2 items-end">
              <Avatar user={currentUser} size="sm" />
              <div className="flex-1">
                <textarea
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  onKeyDown={(e) => { if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) { e.preventDefault(); sendComment() } }}
                  placeholder="Написать комментарий…  (Ctrl+Enter отправить)"
                  rows={2}
                  className="w-full text-sm bg-paper border border-gray-200 rounded-md p-3 outline-none focus:border-ink resize-y"
                />
              </div>
              <Button onClick={sendComment} size="sm" icon={Send}>Отправить</Button>
            </div>
          </div>
        </div>

        <aside className="p-6 space-y-5">
          <Field label="Приоритет">
            <div className="flex flex-wrap gap-1">
              {Object.entries(PRIORITIES).map(([k, v]) => (
                <button
                  key={k}
                  onClick={() => updateTask(task.id, { priority: k })}
                  className={cn(
                    'inline-flex items-center gap-1.5 px-2 py-1 rounded-md text-xs mono border',
                    task.priority === k ? 'border-ink' : 'border-gray-200 hover:border-gray-400'
                  )}
                >
                  <Dot color={v.color} /> {v.label}
                </button>
              ))}
            </div>
          </Field>

          <Field label="Колонка">
            <select
              value={task.columnId}
              onChange={(e) => updateTask(task.id, { columnId: e.target.value })}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            >
              {columns.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </Field>

          <Field label="Исполнитель">
            <select
              value={task.assigneeId || ''}
              onChange={(e) => updateTask(task.id, { assigneeId: e.target.value || null })}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            >
              <option value="">Не назначен</option>
              {Object.values(users).map((u) => <option key={u.id} value={u.id}>{u.username}</option>)}
            </select>
          </Field>

          <Field label="Дедлайн">
            <input
              type="date"
              value={task.deadline ? task.deadline.slice(0, 10) : ''}
              onChange={(e) => updateTask(task.id, { deadline: e.target.value ? new Date(e.target.value).toISOString() : null })}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            />
          </Field>

          <div className="pt-4 border-t border-gray-100">
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-2">Мета</div>
            <div className="text-xs text-gray-600 space-y-1">
              <div className="flex items-center gap-2"><Clock className="w-3 h-3" /> Создано: {formatDate(task.createdAt)}</div>
              {assignee && <div className="flex items-center gap-2"><UserIcon className="w-3 h-3" /> {assignee.username}</div>}
            </div>
          </div>
        </aside>
      </div>
    </Modal>
  )
}

function Field({ label, children }) {
  return (
    <div>
      <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-2">{label}</div>
      {children}
    </div>
  )
}
