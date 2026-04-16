import { cn } from '../../utils/cn'
import { avatarColorFromId, initials } from '../../utils/priorities'

const sizes = {
  xs: 'w-6 h-6 text-[10px]',
  sm: 'w-8 h-8 text-xs',
  md: 'w-10 h-10 text-sm',
  lg: 'w-14 h-14 text-base',
  xl: 'w-20 h-20 text-lg',
  '2xl': 'w-[120px] h-[120px] text-3xl',
}

export function Avatar({ user, size = 'md', className, ring = false }) {
  const src = user?.avatar
  const name = user?.username || user?.name || '?'
  const color = avatarColorFromId(user?.id || name)

  return (
    <div
      className={cn(
        'inline-flex items-center justify-center rounded-full overflow-hidden font-medium select-none',
        ring && 'ring-2 ring-paper',
        sizes[size],
        className
      )}
      style={{ background: color, color: '#0A0A0A' }}
    >
      {src ? (
        <img src={src} alt={name} className="w-full h-full object-cover" />
      ) : (
        <span>{initials(name)}</span>
      )}
    </div>
  )
}

export function AvatarGroup({ users = [], max = 5, size = 'sm', className }) {
  const shown = users.slice(0, max)
  const rest = users.length - shown.length
  return (
    <div className={cn('group/avatargroup inline-flex items-center', className)}>
      {shown.map((u, i) => (
        <div
          key={u.id}
          style={{ marginLeft: i === 0 ? 0 : -8, zIndex: shown.length - i }}
          className="transition-all duration-base ease-out group-hover/avatargroup:ml-0.5"
        >
          <Avatar user={u} size={size} ring />
        </div>
      ))}
      {rest > 0 && (
        <div
          style={{ marginLeft: -8 }}
          className={cn(
            'rounded-full bg-gray-200 text-gray-800 font-medium inline-flex items-center justify-center ring-2 ring-paper',
            sizes[size]
          )}
        >
          +{rest}
        </div>
      )}
    </div>
  )
}
