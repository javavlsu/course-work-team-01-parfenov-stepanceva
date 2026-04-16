import { format, formatDistanceToNow, isPast, differenceInDays, parseISO } from 'date-fns'
import { ru } from 'date-fns/locale'

export function formatDate(date, pattern = 'dd MMM yyyy') {
  if (!date) return ''
  const d = typeof date === 'string' ? parseISO(date) : date
  return format(d, pattern, { locale: ru })
}

export function formatRelative(date) {
  if (!date) return ''
  const d = typeof date === 'string' ? parseISO(date) : date
  return formatDistanceToNow(d, { addSuffix: true, locale: ru })
}

export function deadlineStatus(date) {
  if (!date) return { label: '', color: 'neutral' }
  const d = typeof date === 'string' ? parseISO(date) : date
  if (isPast(d)) return { label: 'Просрочено', color: 'danger' }
  const days = differenceInDays(d, new Date())
  if (days <= 3) return { label: `${days} дн.`, color: 'warning' }
  return { label: formatDate(d, 'dd MMM'), color: 'neutral' }
}
