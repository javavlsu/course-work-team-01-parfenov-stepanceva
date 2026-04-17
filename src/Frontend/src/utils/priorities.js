export const PRIORITIES = {
  LOW: { label: 'LOW', color: '#A8C5A0', dot: 'bg-priority-low' },
  MEDIUM: { label: 'MEDIUM', color: '#D4B896', dot: 'bg-priority-medium' },
  HIGH: { label: 'HIGH', color: '#C4876A', dot: 'bg-priority-high' },
}

export const STATUSES = {
  TODO: { label: 'TO DO', color: '#A0A09E' },
  IN_PROGRESS: { label: 'IN PROGRESS', color: '#7B9EC4' },
  DONE: { label: 'DONE', color: '#8BB89A' },
}

export function avatarColorFromId(id) {
  const palette = ['#A8C5A0', '#D4B896', '#C4876A', '#7B9EC4', '#8BB89A', '#E8E0D5', '#6B6B69', '#3A3A38']
  const str = String(id || 'user')
  let hash = 0
  for (let i = 0; i < str.length; i++) hash = (hash * 31 + str.charCodeAt(i)) | 0
  return palette[Math.abs(hash) % palette.length]
}

export function initials(name = '') {
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (!parts.length) return '?'
  if (parts.length === 1) return parts[0][0].toUpperCase()
  return (parts[0][0] + parts[1][0]).toUpperCase()
}
