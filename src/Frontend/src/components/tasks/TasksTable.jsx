import { useMemo, useState, useEffect } from 'react'
import { Search, ChevronUp, ChevronDown, ChevronLeft, ChevronRight, X } from 'lucide-react'
import { useTasksPage, useColumns } from '../../hooks/useKanban'
import { useBoardUsers } from '../../hooks/useBoards'
import { Avatar } from '../ui/Avatar'
import { Dot } from '../ui/Badge'
import { PRIORITIES, STATUSES } from '../../utils/priorities'
import { deadlineStatus, formatDate } from '../../utils/dates'
import { useTranslation } from '../../i18n'
import { cn } from '../../utils/cn'

const PAGE_SIZE = 10

export function TasksTable({ boardId, onOpenTask }) {
  const { data: columns = [] } = useColumns(boardId)
  const { data: boardUsers = [] } = useBoardUsers(boardId)
  const { t } = useTranslation()

  const sortableColumns = [
    { key: 'title', label: t('tasksTable.colTitle') },
    { key: 'status', label: t('tasksTable.colStatus') },
    { key: 'priority', label: t('tasksTable.colPriority') },
    { key: 'deadline', label: t('tasksTable.colDeadline') },
    { key: 'createdAt', label: t('tasksTable.colCreated') },
  ]

  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')
  const [filters, setFilters] = useState({
    priority: '',
    status: '',
    columnId: '',
    assigneeId: '',
    sortBy: 'createdAt',
    sortDir: 'desc',
  })
  const [page, setPage] = useState(0)
  const { priority, status, columnId, assigneeId, sortBy, sortDir } = filters

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search.trim())
      setPage(0)
    }, 300)
    return () => clearTimeout(timer)
  }, [search])

  const patchFilters = (patch) => {
    setFilters((prev) => ({ ...prev, ...patch }))
    setPage(0)
  }

  const params = useMemo(() => ({
    search: debouncedSearch || undefined,
    priority: priority || undefined,
    status: status || undefined,
    columnId: columnId ? Number(columnId) : undefined,
    assigneeId: assigneeId ? Number(assigneeId) : undefined,
    sortBy,
    sortDir,
    page,
    size: PAGE_SIZE,
  }), [debouncedSearch, priority, status, columnId, assigneeId, sortBy, sortDir, page])

  const { data, isLoading, isFetching, isError } = useTasksPage(boardId, params)

  const tasks = data?.content || []
  const totalElements = data?.totalElements || 0
  const totalPages = data?.totalPages || 0
  const currentPage = data?.page ?? page

  const usersById = useMemo(
    () => Object.fromEntries(boardUsers.map((u) => [u.id, u])),
    [boardUsers]
  )
  const columnById = useMemo(
    () => Object.fromEntries(columns.map((c) => [c.id, c])),
    [columns]
  )

  const toggleSort = (key) => {
    if (sortBy === key) {
      patchFilters({ sortDir: sortDir === 'asc' ? 'desc' : 'asc' })
    } else {
      patchFilters({ sortBy: key, sortDir: 'asc' })
    }
  }

  const resetFilters = () => {
    setSearch('')
    setFilters({ priority: '', status: '', columnId: '', assigneeId: '', sortBy: 'createdAt', sortDir: 'desc' })
    setPage(0)
  }

  const hasFilters = search || priority || status || columnId || assigneeId

  return (
    <div className="h-full overflow-y-auto px-6 md:px-12 pb-8">
      <div className="flex flex-wrap items-center gap-3 py-4">
        <div className="relative flex-1 min-w-[220px] max-w-md">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder={t('tasksTable.searchPlaceholder')}
            className="w-full h-10 pl-9 pr-9 bg-paper border border-gray-200 rounded-md text-sm outline-none focus:border-ink"
          />
          {search && (
            <button
              onClick={() => setSearch('')}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-ink"
              aria-label={t('common.reset')}
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        <FilterSelect value={priority} onChange={(v) => patchFilters({ priority: v })} placeholder={t('tasksTable.allPriorities')}>
          {Object.keys(PRIORITIES).map((k) => (
            <option key={k} value={k}>{t(`priorities.${k}`)}</option>
          ))}
        </FilterSelect>

        <FilterSelect value={status} onChange={(v) => patchFilters({ status: v })} placeholder={t('tasksTable.allStatuses')}>
          {Object.keys(STATUSES).map((k) => (
            <option key={k} value={k}>{t(`statuses.${k}`)}</option>
          ))}
        </FilterSelect>

        <FilterSelect value={columnId} onChange={(v) => patchFilters({ columnId: v })} placeholder={t('tasksTable.allColumns')}>
          {columns.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </FilterSelect>

        <FilterSelect value={assigneeId} onChange={(v) => patchFilters({ assigneeId: v })} placeholder={t('tasksTable.allAssignees')}>
          {boardUsers.map((u) => (
            <option key={u.id} value={u.id}>{u.username}</option>
          ))}
        </FilterSelect>

        {hasFilters && (
          <button
            onClick={resetFilters}
            className="text-xs mono tracking-[0.08em] uppercase text-gray-500 hover:text-ink inline-flex items-center gap-1"
          >
            <X className="w-3 h-3" /> {t('tasksTable.resetFilters')}
          </button>
        )}
      </div>

      <div className="rounded-lg border border-gray-100 bg-paper overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                {sortableColumns.map((col, i) => (
                  <th
                    key={col.key}
                    onClick={() => toggleSort(col.key)}
                    className={cn(
                      'text-left px-4 py-3 mono text-[11px] tracking-[0.1em] uppercase text-gray-500 cursor-pointer select-none whitespace-nowrap hover:text-ink',
                      i === 0 && 'pl-6'
                    )}
                  >
                    <span className="inline-flex items-center gap-1">
                      {col.label}
                      <SortIcon active={sortBy === col.key} dir={sortDir} />
                    </span>
                  </th>
                ))}
                <th className="text-left px-4 py-3 mono text-[11px] tracking-[0.1em] uppercase text-gray-500 whitespace-nowrap">
                  {t('tasksTable.colColumn')}
                </th>
                <th className="text-left px-4 py-3 mono text-[11px] tracking-[0.1em] uppercase text-gray-500 whitespace-nowrap pr-6">
                  {t('tasksTable.colAssignee')}
                </th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <tr key={i} className="border-b border-gray-50">
                    <td colSpan={7} className="px-4 py-3">
                      <div className="h-5 skeleton rounded" />
                    </td>
                  </tr>
                ))
              ) : isError ? (
                <tr><td colSpan={7} className="px-6 py-10 text-center text-priority-high">{t('tasksTable.loadFailed')}</td></tr>
              ) : tasks.length === 0 ? (
                <tr><td colSpan={7} className="px-6 py-10 text-center text-gray-400">{t('tasksTable.emptyState')}</td></tr>
              ) : (
                tasks.map((task) => (
                  <TaskRow
                    key={task.id}
                    task={task}
                    column={columnById[task.columnId]}
                    assignee={task.assignee || usersById[task.assigneeId]}
                    onClick={() => onOpenTask?.(task)}
                  />
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100 text-xs text-gray-500">
          <div className="mono tracking-[0.08em]">
            {isFetching && !isLoading ? (
              <span className="text-gray-400">{t('tasksTable.refreshing')}</span>
            ) : (
              <span>
                {totalElements === 0
                  ? t('tasksTable.noResults')
                  : t('tasksTable.countLabel', {
                      from: currentPage * PAGE_SIZE + 1,
                      to: Math.min((currentPage + 1) * PAGE_SIZE, totalElements),
                      total: totalElements,
                    })}
              </span>
            )}
          </div>
          <div className="flex items-center gap-2">
            <button
              disabled={currentPage === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              className="w-8 h-8 inline-flex items-center justify-center rounded-md border border-gray-200 disabled:opacity-40 disabled:cursor-not-allowed hover:border-ink"
              aria-label={t('common.previous')}
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="mono text-xs px-2">
              {totalPages === 0 ? '0 / 0' : `${currentPage + 1} / ${totalPages}`}
            </span>
            <button
              disabled={currentPage + 1 >= totalPages}
              onClick={() => setPage((p) => p + 1)}
              className="w-8 h-8 inline-flex items-center justify-center rounded-md border border-gray-200 disabled:opacity-40 disabled:cursor-not-allowed hover:border-ink"
              aria-label={t('common.next')}
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

function FilterSelect({ value, onChange, placeholder, children }) {
  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="h-10 px-3 bg-paper border border-gray-200 rounded-md text-sm outline-none focus:border-ink mono tracking-[0.04em]"
    >
      <option value="">{placeholder}</option>
      {children}
    </select>
  )
}

function SortIcon({ active, dir }) {
  if (!active) return <ChevronDown className="w-3 h-3 opacity-30" />
  return dir === 'asc' ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />
}

function TaskRow({ task, column, assignee, onClick }) {
  const { t } = useTranslation()
  const p = PRIORITIES[task.priority] || PRIORITIES.MEDIUM
  const s = STATUSES[task.status] || STATUSES.TODO
  const dl = deadlineStatus(task.deadline)

  return (
    <tr
      onClick={onClick}
      className="border-b border-gray-50 hover:bg-gray-50 cursor-pointer transition-colors"
    >
      <td className="px-4 py-3 pl-6 max-w-[360px]">
        <div className="text-ink font-medium truncate">{task.title}</div>
        {task.description && (
          <div className="text-xs text-gray-500 truncate">{task.description}</div>
        )}
      </td>
      <td className="px-4 py-3 whitespace-nowrap">
        <span className="inline-flex items-center gap-1.5 text-xs mono tracking-[0.08em] uppercase">
          <Dot color={s.color} /> {t(`statuses.${task.status}`)}
        </span>
      </td>
      <td className="px-4 py-3 whitespace-nowrap">
        <span className="inline-flex items-center gap-1.5 text-xs mono tracking-[0.08em] uppercase">
          <Dot color={p.color} /> {t(`priorities.${task.priority}`)}
        </span>
      </td>
      <td className="px-4 py-3 whitespace-nowrap">
        {task.deadline ? (
          <span className={cn(
            'text-xs',
            dl.color === 'danger' && 'text-priority-high',
            dl.color === 'warning' && 'text-priority-medium',
            dl.color === 'neutral' && 'text-gray-600'
          )}>
            {formatDate(task.deadline, 'dd MMM yyyy')}
          </span>
        ) : (
          <span className="text-xs text-gray-300">—</span>
        )}
      </td>
      <td className="px-4 py-3 whitespace-nowrap text-xs text-gray-600">
        {task.createdAt ? formatDate(task.createdAt, 'dd MMM yyyy') : '—'}
      </td>
      <td className="px-4 py-3 whitespace-nowrap">
        {column ? (
          <span className="mono text-xs tracking-[0.08em] uppercase text-gray-600">{column.name}</span>
        ) : (
          <span className="text-xs text-gray-300">—</span>
        )}
      </td>
      <td className="px-4 py-3 pr-6 whitespace-nowrap">
        {assignee ? (
          <div className="inline-flex items-center gap-2">
            <Avatar user={assignee} size="xs" />
            <span className="text-xs text-gray-700 truncate max-w-[120px]">{assignee.username}</span>
          </div>
        ) : (
          <span className="text-xs text-gray-300">{t('tasks.unassigned')}</span>
        )}
      </td>
    </tr>
  )
}
