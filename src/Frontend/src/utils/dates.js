import { format, formatDistanceToNow, isPast, differenceInDays, parseISO } from 'date-fns'
import { ru, enUS } from 'date-fns/locale'
import { useI18nStore } from '../i18n/store'

const localeMap = { ru, en: enUS }

function currentLocale() {
  return localeMap[useI18nStore.getState().locale] || ru
}

export function formatDate(date, pattern = 'dd MMM yyyy') {
  if (!date) return ''
  const d = typeof date === 'string' ? parseISO(date) : date
  return format(d, pattern, { locale: currentLocale() })
}

export function formatRelative(date) {
  if (!date) return ''
  const d = typeof date === 'string' ? parseISO(date) : date
  return formatDistanceToNow(d, { addSuffix: true, locale: currentLocale() })
}

// Возвращает структурированный статус — компонент сам берёт локализацию через t().
export function deadlineStatus(date) {
  if (!date) return { kind: 'none', color: 'neutral' }
  const d = typeof date === 'string' ? parseISO(date) : date
  if (isPast(d)) return { kind: 'overdue', color: 'danger' }
  const days = differenceInDays(d, new Date())
  if (days <= 3) return { kind: 'soon', color: 'warning', days }
  return { kind: 'future', color: 'neutral', date: d }
}
