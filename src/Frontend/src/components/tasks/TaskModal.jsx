import { useEffect, useMemo, useRef, useState } from 'react'
import { Trash2, Clock, User as UserIcon, Send, Paperclip, X, Download, History } from 'lucide-react'
import { Modal } from '../ui/Modal'
import { Button } from '../ui/Button'
import { Avatar } from '../ui/Avatar'
import { Dot } from '../ui/Badge'
import { useAuthStore } from '../../store/authStore'
import { useColumns, useTasks, useUpdateTask, useDeleteTask } from '../../hooks/useKanban'
import { useComments, useCreateComment, useDeleteComment } from '../../hooks/useComments'
import { useAttachments, useUploadAttachment, useDeleteAttachment } from '../../hooks/useAttachments'
import { useTaskHistory } from '../../hooks/useTaskHistory'
import { useBoardUsers } from '../../hooks/useBoards'
import { PRIORITIES } from '../../utils/priorities'
import { formatDate, formatRelative } from '../../utils/dates'
import { cn } from '../../utils/cn'
import { toast } from 'sonner'
import { API_BASE } from '../../api/client'

export function TaskModal({ boardId, taskId, open, onClose }) {
  const { data: tasks = [] } = useTasks(boardId)
  const { data: columns = [] } = useColumns(boardId)
  const { data: boardUsers = [] } = useBoardUsers(boardId)
  const { data: comments = [] } = useComments(boardId, taskId)

  const updateTask = useUpdateTask(boardId)
  const deleteTask = useDeleteTask(boardId)
  const createComment = useCreateComment(boardId, taskId)
  const removeComment = useDeleteComment(boardId, taskId)
  const { data: attachments = [] } = useAttachments(boardId, taskId)
  const uploadAttachment = useUploadAttachment(boardId, taskId)
  const removeAttachment = useDeleteAttachment(boardId, taskId)

  const currentUser = useAuthStore((s) => s.user)
  const task = useMemo(() => tasks.find((t) => t.id === taskId), [tasks, taskId])
  const sortedColumns = useMemo(() => [...columns].sort((a, b) => a.order - b.order), [columns])
  const { data: history = [] } = useTaskHistory(boardId, taskId)
  const fileRef = useRef(null)

  const [tab, setTab] = useState('main')
  const [title, setTitle] = useState('')
  const [editingTitle, setEditingTitle] = useState(false)
  const [desc, setDesc] = useState('')
  const [editingDesc, setEditingDesc] = useState(false)
  const [comment, setComment] = useState('')

  useEffect(() => {
    if (task) { setTitle(task.title); setDesc(task.description || '') }
  }, [taskId, task?.id])

  if (!task) return null

  const saveTitle = () => {
    const trimmed = title.trim()
    if (trimmed && trimmed !== task.title) {
      updateTask.mutate({ taskId: task.id, data: { title: trimmed } })
    }
    setEditingTitle(false)
  }
  const saveDesc = () => {
    if (desc !== (task.description || '')) {
      updateTask.mutate({ taskId: task.id, data: { description: desc } })
    }
    setEditingDesc(false)
  }

  const sendComment = () => {
    const trimmed = comment.trim()
    if (!trimmed) return
    createComment.mutate(trimmed, { onSuccess: () => setComment('') })
  }

  const assignee = task.assignee || boardUsers.find((u) => u.id === task.assigneeId)
  const p = PRIORITIES[task.priority] || PRIORITIES.MEDIUM

  return (
    <Modal open={open} onClose={onClose} size="lg" hideClose>
      <div className="px-6 pt-5 pb-0 flex items-center justify-between border-b border-gray-100">
        <div className="flex items-center gap-5">
          {[
            { id: 'main', label: 'Задача' },
            { id: 'history', label: 'История', icon: History },
          ].map((t) => (
            <button
              key={t.id}
              onClick={() => setTab(t.id)}
              className={cn(
                'pb-4 relative mono text-xs tracking-[0.1em] uppercase transition-colors inline-flex items-center gap-1.5',
                tab === t.id ? 'text-ink' : 'text-gray-400 hover:text-gray-600'
              )}
            >
              {t.icon && <t.icon className="w-3 h-3" />}
              {t.label}
              {tab === t.id && <span className="absolute left-0 right-0 -bottom-px h-[2px] bg-ink" />}
            </button>
          ))}
        </div>
        <div className="flex items-center gap-1 pb-4">
          <button
            onClick={() => {
              if (confirm('Удалить задачу?')) {
                deleteTask.mutate(task.id, { onSuccess: onClose })
              }
            }}
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

      {tab === 'history' && (
        <div className="p-6 max-h-[78vh] overflow-y-auto">
          <HistoryTab history={history} />
        </div>
      )}

      <div className={cn('grid md:grid-cols-[1fr_240px] gap-0 max-h-[78vh] overflow-y-auto', tab !== 'main' && 'hidden')}>
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
            <div className="flex items-center justify-between mb-3">
              <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400">Вложения</div>
              <button
                onClick={() => fileRef.current?.click()}
                className="inline-flex items-center gap-1 text-xs text-gray-500 hover:text-ink border border-gray-200 hover:border-ink rounded-md px-2 py-1 transition-colors"
              >
                <Paperclip className="w-3 h-3" /> Прикрепить
              </button>
              <input
                ref={fileRef}
                type="file"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files?.[0]
                  if (file) {
                    if (file.size > 20 * 1024 * 1024) { toast.error('Файл слишком большой (max 20 MB)'); return }
                    uploadAttachment.mutate(file)
                    e.target.value = ''
                  }
                }}
              />
            </div>
            {attachments.length === 0 && !uploadAttachment.isPending && (
              <div className="text-sm text-gray-400 mb-4">Нет вложений</div>
            )}
            {uploadAttachment.isPending && (
              <div className="text-sm text-gray-400 mb-2">Загрузка…</div>
            )}
            <div className="space-y-2 mb-4">
              {attachments.map((a) => (
                <div key={a.id} className="flex items-center gap-2 text-sm group/att border border-gray-100 rounded-md px-3 py-2 hover:bg-gray-50">
                  <Paperclip className="w-3.5 h-3.5 text-gray-400 shrink-0" />
                  <span className="flex-1 truncate text-gray-800">{a.fileName}</span>
                  <span className="text-xs text-gray-400 shrink-0">{formatDate(a.uploadedAt)}</span>
                  <a
                    href={`${API_BASE}/files/${a.storageKey}`}
                    download={a.fileName}
                    className="text-gray-400 hover:text-ink opacity-0 group-hover/att:opacity-100 transition-opacity"
                    aria-label="Скачать"
                  >
                    <Download className="w-3.5 h-3.5" />
                  </a>
                  <button
                    onClick={() => removeAttachment.mutate(a.id)}
                    className="text-gray-400 hover:text-priority-high opacity-0 group-hover/att:opacity-100 transition-opacity"
                    aria-label="Удалить"
                  >
                    <X className="w-3.5 h-3.5" />
                  </button>
                </div>
              ))}
            </div>
          </div>

          <div>
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-3">Комментарии</div>
            <div className="space-y-4 mb-4">
              {comments.length === 0 && <div className="text-sm text-gray-400">Пока нет комментариев</div>}
              {comments.map((c) => {
                const mine = c.authorId === currentUser?.id
                return (
                  <div key={c.id} className="flex gap-3 group/comment">
                    <Avatar user={c.author} size="sm" />
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-sm font-medium">{c.author?.username}</span>
                        <span className="text-xs text-gray-400" title={formatDate(c.createdAt, 'dd MMM yyyy, HH:mm')}>{formatRelative(c.createdAt)}</span>
                        {mine && (
                          <button
                            onClick={() => removeComment.mutate(c.id)}
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
              <Button onClick={sendComment} size="sm" icon={Send} loading={createComment.isPending}>Отправить</Button>
            </div>
          </div>
        </div>

        <aside className="p-6 space-y-5">
          <Field label="Приоритет">
            <div className="flex flex-wrap gap-1">
              {Object.entries(PRIORITIES).map(([k, v]) => (
                <button
                  key={k}
                  onClick={() => updateTask.mutate({ taskId: task.id, data: { priority: k } })}
                  style={task.priority === k ? { backgroundColor: v.color + '33', borderColor: v.color } : {}}
                  className={cn(
                    'inline-flex items-center gap-1.5 px-2 py-1 rounded-md text-xs mono border transition-all',
                    task.priority === k ? 'font-semibold' : 'border-gray-200 hover:border-gray-400'
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
              onChange={(e) => updateTask.mutate({ taskId: task.id, data: { columnId: Number(e.target.value) || e.target.value } })}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            >
              {sortedColumns.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </Field>

          <Field label="Исполнитель">
            <select
              value={task.assigneeId || ''}
              onChange={(e) => {
                const v = e.target.value
                updateTask.mutate({ taskId: task.id, data: { assigneeId: v ? (Number(v) || v) : null } })
              }}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            >
              <option value="">Не назначен</option>
              {boardUsers.map((u) => <option key={u.id} value={u.id}>{u.username}</option>)}
            </select>
          </Field>

          <Field label="Дедлайн">
            <input
              type="datetime-local"
              value={task.deadline ? task.deadline.slice(0, 16) : ''}
              onChange={(e) => {
                const v = e.target.value
                if (!v) {
                  updateTask.mutate({ taskId: task.id, data: { deadline: null } })
                } else {
                  const iso = new Date(v).toISOString()
                  const local = new Date(v)
                  if (local.getTime() < Date.now()) {
                    toast.error('Дедлайн должен быть в будущем')
                    return
                  }
                  updateTask.mutate({ taskId: task.id, data: { deadline: iso.slice(0, 19) } })
                }
              }}
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

const ATTR_LABELS = {
  TASK: 'Задача', TITLE: 'Заголовок', DESCRIPTION: 'Описание',
  COLUMN: 'Колонка', ASSIGNEE: 'Исполнитель', POSITION: 'Позиция',
  DEADLINE: 'Дедлайн', PRIORITY: 'Приоритет', STATUS: 'Статус',
}

function HistoryTab({ history }) {
  if (history.length === 0) {
    return <div className="text-sm text-gray-400 py-4">История изменений пуста</div>
  }
  return (
    <div className="space-y-3">
      {history.map((h) => (
        <div key={h.id} className="flex gap-3">
          <Avatar user={h.user} size="sm" />
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2 flex-wrap mb-1">
              <span className="text-sm font-medium">{h.user?.username || 'System'}</span>
              <span className="mono text-[10px] tracking-[0.08em] uppercase text-gray-400">
                {ATTR_LABELS[h.changedAttribute] || h.changedAttribute}
              </span>
              <span className="text-xs text-gray-400 ml-auto">{formatRelative(h.changedAt)}</span>
            </div>
            {h.actionType === 'create' ? (
              <div className="text-xs text-gray-600">Задача создана</div>
            ) : (
              <div className="text-xs text-gray-600 flex items-center gap-1.5 flex-wrap">
                {h.oldValue && (
                  <span className="line-through text-gray-400 truncate max-w-[140px]" title={h.oldValue}>{h.oldValue}</span>
                )}
                {h.oldValue && <span className="text-gray-300">→</span>}
                {h.newValue && (
                  <span className="text-gray-800 truncate max-w-[140px]" title={h.newValue}>{h.newValue}</span>
                )}
              </div>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}
