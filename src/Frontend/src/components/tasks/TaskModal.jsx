import { useEffect, useMemo, useRef, useState } from 'react'
import { Trash2, Clock, User as UserIcon, Send, Paperclip, X, Download, History, Pencil, Check } from 'lucide-react'
import { Modal } from '../ui/Modal'
import { Button } from '../ui/Button'
import { Avatar } from '../ui/Avatar'
import { Dot } from '../ui/Badge'
import { useAuthStore } from '../../store/authStore'
import { useColumns, useTasks, useUpdateTask, useDeleteTask } from '../../hooks/useKanban'
import { useComments, useCreateComment, useDeleteComment, useUpdateComment } from '../../hooks/useComments'
import { useAttachments, useUploadAttachment, useDeleteAttachment } from '../../hooks/useAttachments'
import { useTaskHistory } from '../../hooks/useTaskHistory'
import { useBoardUsers } from '../../hooks/useBoards'
import { PRIORITIES } from '../../utils/priorities'
import { formatDate, formatRelative } from '../../utils/dates'
import { useTranslation } from '../../i18n'
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
  const editComment = useUpdateComment(boardId, taskId)
  const { data: attachments = [] } = useAttachments(boardId, taskId)
  const uploadAttachment = useUploadAttachment(boardId, taskId)
  const removeAttachment = useDeleteAttachment(boardId, taskId)
  const { t } = useTranslation()

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
  const [editingCommentId, setEditingCommentId] = useState(null)
  const [editCommentText, setEditCommentText] = useState('')

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

  return (
    <Modal open={open} onClose={onClose} size="lg" hideClose>
      <div className="px-6 pt-5 pb-0 flex items-center justify-between border-b border-gray-100">
        <div className="flex items-center gap-5">
          {[
            { id: 'main', label: t('tasks.columnTask') },
            { id: 'history', label: t('tasks.history'), icon: History },
          ].map((tabItem) => (
            <button
              key={tabItem.id}
              onClick={() => setTab(tabItem.id)}
              className={cn(
                'pb-4 relative mono text-xs tracking-[0.1em] uppercase transition-colors inline-flex items-center gap-1.5',
                tab === tabItem.id ? 'text-ink' : 'text-gray-400 hover:text-gray-600'
              )}
            >
              {tabItem.icon && <tabItem.icon className="w-3 h-3" />}
              {tabItem.label}
              {tab === tabItem.id && <span className="absolute left-0 right-0 -bottom-px h-[2px] bg-ink" />}
            </button>
          ))}
        </div>
        <div className="flex items-center gap-1 pb-4">
          <button
            onClick={() => {
              if (confirm(t('tasks.deleteConfirm'))) {
                deleteTask.mutate(task.id, { onSuccess: onClose })
              }
            }}
            className="w-8 h-8 inline-flex items-center justify-center rounded-md text-gray-400 hover:text-priority-high hover:bg-priority-high/10"
            aria-label={t('common.delete')}
          >
            <Trash2 className="w-4 h-4" />
          </button>
          <button onClick={onClose} className="w-8 h-8 inline-flex items-center justify-center rounded-md hover:bg-gray-100" aria-label={t('common.close')}>
            ✕
          </button>
        </div>
      </div>

      {tab === 'history' && (
        <div className="p-6 max-h-[78vh] overflow-y-auto">
          <HistoryTab history={history} columns={sortedColumns} />
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
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-2">{t('tasks.description')}</div>
            {editingDesc ? (
              <textarea
                value={desc}
                onChange={(e) => setDesc(e.target.value)}
                onBlur={saveDesc}
                autoFocus
                rows={5}
                className="w-full text-sm bg-paper border border-gray-200 rounded-md p-3 outline-none focus:border-ink resize-y"
                placeholder={t('tasks.descriptionPlaceholder')}
              />
            ) : (
              <div
                onClick={() => setEditingDesc(true)}
                className="cursor-text text-sm leading-relaxed min-h-[60px] p-3 rounded-md hover:bg-gray-100/50 whitespace-pre-wrap break-all"
              >
                {task.description || <span className="text-gray-400">{t('tasks.descriptionPlaceholder')}</span>}
              </div>
            )}
          </div>

          <div>
            <div className="flex items-center justify-between mb-3">
              <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400">{t('tasks.attachments')}</div>
              <button
                onClick={() => fileRef.current?.click()}
                className="inline-flex items-center gap-1 text-xs text-gray-500 hover:text-ink border border-gray-200 hover:border-ink rounded-md px-2 py-1 transition-colors"
              >
                <Paperclip className="w-3 h-3" /> {t('tasks.attach')}
              </button>
              <input
                ref={fileRef}
                type="file"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files?.[0]
                  if (file) {
                    if (file.size > 20 * 1024 * 1024) { toast.error(t('tasks.fileTooLarge', { max: 20 })); return }
                    uploadAttachment.mutate(file)
                    e.target.value = ''
                  }
                }}
              />
            </div>
            {attachments.length === 0 && !uploadAttachment.isPending && (
              <div className="text-sm text-gray-400 mb-4">{t('tasks.noAttachments')}</div>
            )}
            {uploadAttachment.isPending && (
              <div className="text-sm text-gray-400 mb-2">{t('tasks.uploading')}</div>
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
                    aria-label={t('common.actions')}
                  >
                    <Download className="w-3.5 h-3.5" />
                  </a>
                  <button
                    onClick={() => removeAttachment.mutate(a.id)}
                    className="text-gray-400 hover:text-priority-high opacity-0 group-hover/att:opacity-100 transition-opacity"
                    aria-label={t('common.delete')}
                  >
                    <X className="w-3.5 h-3.5" />
                  </button>
                </div>
              ))}
            </div>
          </div>

          <div>
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-3">{t('tasks.comments')}</div>
            <div className="space-y-4 mb-4">
              {comments.length === 0 && <div className="text-sm text-gray-400">{t('tasks.noComments')}</div>}
              {comments.map((c) => {
                const mine = c.authorId === currentUser?.id
                const isEditing = editingCommentId === c.id
                return (
                  <div key={c.id} className="flex gap-3 group/comment">
                    <Avatar user={c.author} size="sm" />
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-sm font-medium">{c.author?.username}</span>
                        <span className="text-xs text-gray-400" title={formatDate(c.createdAt, 'dd MMM yyyy, HH:mm')}>{formatRelative(c.createdAt)}</span>
                        {c.updatedAt && c.updatedAt !== c.createdAt && (
                          <span className="text-xs text-gray-300">{t('tasks.commentEdited')}</span>
                        )}
                        {mine && !isEditing && (
                          <div className="ml-auto flex items-center gap-2 opacity-0 group-hover/comment:opacity-100 transition-opacity">
                            <button
                              onClick={() => { setEditingCommentId(c.id); setEditCommentText(c.text) }}
                              className="text-xs text-gray-400 hover:text-ink inline-flex items-center gap-1"
                            ><Pencil className="w-3 h-3" /> {t('common.edit')}</button>
                            <button
                              onClick={() => removeComment.mutate(c.id)}
                              className="text-xs text-gray-400 hover:text-priority-high"
                            >{t('common.delete')}</button>
                          </div>
                        )}
                      </div>
                      {isEditing ? (
                        <div className="space-y-2">
                          <textarea
                            value={editCommentText}
                            onChange={(e) => setEditCommentText(e.target.value)}
                            autoFocus
                            rows={2}
                            className="w-full text-sm bg-paper border border-gray-200 rounded-md p-2 outline-none focus:border-ink resize-y"
                          />
                          <div className="flex gap-1">
                            <button
                              onClick={() => {
                                const trimmed = editCommentText.trim()
                                if (trimmed && trimmed !== c.text) {
                                  editComment.mutate({ commentId: c.id, text: trimmed }, {
                                    onSuccess: () => setEditingCommentId(null),
                                  })
                                } else {
                                  setEditingCommentId(null)
                                }
                              }}
                              className="w-6 h-6 inline-flex items-center justify-center rounded bg-ink text-paper hover:bg-gray-800"
                            ><Check className="w-3 h-3" /></button>
                            <button
                              onClick={() => setEditingCommentId(null)}
                              className="w-6 h-6 inline-flex items-center justify-center rounded hover:bg-gray-100"
                            ><X className="w-3 h-3" /></button>
                          </div>
                        </div>
                      ) : (
                        <div className="text-sm text-gray-800 whitespace-pre-wrap break-words">{c.text}</div>
                      )}
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
                  placeholder={t('tasks.commentPlaceholder')}
                  rows={2}
                  className="w-full text-sm bg-paper border border-gray-200 rounded-md p-3 outline-none focus:border-ink resize-y"
                />
              </div>
              <Button onClick={sendComment} size="sm" icon={Send} loading={createComment.isPending}>{t('tasks.commentSend')}</Button>
            </div>
          </div>
        </div>

        <aside className="p-6 space-y-5">
          <Field label={t('tasks.priority')}>
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
                  <Dot color={v.color} /> {t(`priorities.${k}`)}
                </button>
              ))}
            </div>
          </Field>

          <Field label={t('tasks.column')}>
            <select
              value={task.columnId}
              onChange={(e) => updateTask.mutate({ taskId: task.id, data: { columnId: Number(e.target.value) || e.target.value } })}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            >
              {sortedColumns.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </Field>

          <Field label={t('tasks.assignee')}>
            <select
              value={task.assigneeId || ''}
              onChange={(e) => {
                const v = e.target.value
                updateTask.mutate({ taskId: task.id, data: { assigneeId: v ? (Number(v) || v) : null } })
              }}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            >
              <option value="">{t('tasks.unassigned')}</option>
              {boardUsers.map((u) => <option key={u.id} value={u.id}>{u.username}</option>)}
            </select>
          </Field>

          <Field label={t('tasks.deadline')}>
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
                    toast.error(t('tasks.deadlineFuture'))
                    return
                  }
                  updateTask.mutate({ taskId: task.id, data: { deadline: iso.slice(0, 19) } })
                }
              }}
              className="w-full bg-paper border border-gray-200 rounded-md px-3 py-2 text-sm outline-none focus:border-ink"
            />
          </Field>

          <div className="pt-4 border-t border-gray-100">
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-2">{t('tasks.meta')}</div>
            <div className="text-xs text-gray-600 space-y-1">
              <div className="flex items-center gap-2"><Clock className="w-3 h-3" /> {t('tasks.createdAt')}: {formatDate(task.createdAt)}</div>
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

function HistoryTab({ history, columns = [] }) {
  const { t } = useTranslation()
  const columnById = useMemo(
    () => Object.fromEntries(columns.map((c) => [String(c.id), c.name])),
    [columns]
  )

  const resolveValue = (attr, value) => {
    if (!value) return value
    if (attr === 'COLUMN') return columnById[String(value)] || value
    if (attr === 'PRIORITY') {
      const key = String(value).toUpperCase()
      return t(`priorities.${key}`)
    }
    if (attr === 'STATUS') {
      const key = String(value).toUpperCase()
      return t(`statuses.${key}`)
    }
    return value
  }

  if (history.length === 0) {
    return <div className="text-sm text-gray-400 py-4">{t('tasks.historyEmpty')}</div>
  }

  return (
    <div className="space-y-3">
      {history.map((h) => {
        const attr = h.changedAttribute
        const isCreateEvent = h.actionType === 'create'
        const isDeleteEvent = h.actionType === 'delete'
        const simpleLabel =
          isCreateEvent ? t(`history.create.${attr}`, {}) || t('history.create.TASK') :
          isDeleteEvent ? t(`history.delete.${attr}`, {}) : null
        // вернёт ключ если перевода нет — подменяем на null
        const renderedSimple = simpleLabel && !simpleLabel.startsWith('history.') ? simpleLabel : null

        return (
          <div key={h.id} className="flex gap-3">
            <Avatar user={h.user} size="sm" />
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 flex-wrap mb-1">
                <span className="text-sm font-medium">{h.user?.username || t('history.system')}</span>
                {attr && !renderedSimple && (
                  <span className="mono text-[10px] tracking-[0.08em] uppercase text-gray-400">
                    {t(`history.attr.${attr}`)}
                  </span>
                )}
                <span className="text-xs text-gray-400 ml-auto">{formatRelative(h.changedAt)}</span>
              </div>
              {renderedSimple ? (
                <div className="text-xs text-gray-600">{renderedSimple}</div>
              ) : (
                <div className="text-xs text-gray-600 flex items-center gap-1.5 flex-wrap">
                  {h.oldValue && (
                    <span className="line-through text-gray-400 truncate max-w-[140px]" title={resolveValue(attr, h.oldValue)}>
                      {resolveValue(attr, h.oldValue)}
                    </span>
                  )}
                  {h.oldValue && <span className="text-gray-300">→</span>}
                  {h.newValue && (
                    <span className="text-gray-800 truncate max-w-[140px]" title={resolveValue(attr, h.newValue)}>
                      {resolveValue(attr, h.newValue)}
                    </span>
                  )}
                </div>
              )}
            </div>
          </div>
        )
      })}
    </div>
  )
}
