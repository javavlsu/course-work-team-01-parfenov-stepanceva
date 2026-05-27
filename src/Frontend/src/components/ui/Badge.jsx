import { cn } from '../../utils/cn'

const variants = {
  default: 'bg-gray-100 text-gray-800',
  success: 'bg-status-done/30 text-ink',
  warning: 'bg-priority-medium/40 text-ink',
  danger: 'bg-priority-high/30 text-ink',
  info: 'bg-status-progress/30 text-ink',
  ink: 'bg-ink text-paper',
}

export function Badge({ variant = 'default', className, children }) {
  return (
    <span className={cn('inline-flex items-center gap-1 px-2 py-[2px] rounded-full text-xs font-medium', variants[variant], className)}>
      {children}
    </span>
  )
}

export function Dot({ color = '#A0A09E', className }) {
  return (
    <span
      aria-hidden
      className={cn('inline-block w-2 h-2 rounded-full', className)}
      style={{ background: color }}
    />
  )
}
