import { motion } from 'framer-motion'
import { Loader2 } from 'lucide-react'
import { cn } from '../../utils/cn'

const variants = {
  primary: 'bg-ink text-paper hover:bg-gray-800 shadow-sm hover:shadow-md',
  secondary: 'bg-paper text-ink border border-ink hover:bg-ink hover:text-paper',
  ghost: 'bg-transparent text-gray-600 hover:text-ink hover:bg-gray-100',
  danger: 'bg-priority-high text-paper hover:brightness-95',
  outline: 'bg-transparent text-ink border border-gray-200 hover:border-ink hover:bg-gray-100',
}

const sizes = {
  sm: 'h-8 px-3 text-sm',
  md: 'h-10 px-5 text-sm',
  lg: 'h-[52px] px-6 text-base',
}

export function Button({
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  icon: Icon,
  iconRight: IconRight,
  className,
  children,
  onClick,
  type = 'button',
  fullWidth = false,
  ...rest
}) {
  const isDisabled = disabled || loading
  return (
    <motion.button
      type={type}
      whileHover={!isDisabled ? { y: -1 } : undefined}
      whileTap={!isDisabled ? { y: 0, scale: 0.99 } : undefined}
      transition={{ duration: 0.15, ease: [0.16, 1, 0.3, 1] }}
      onClick={onClick}
      disabled={isDisabled}
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-md font-medium transition-colors duration-base ease-out',
        'disabled:cursor-not-allowed disabled:opacity-40',
        'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ink/40 focus-visible:ring-offset-2 focus-visible:ring-offset-paper',
        variants[variant],
        sizes[size],
        fullWidth && 'w-full',
        className
      )}
      {...rest}
    >
      {loading ? (
        <Loader2 className="w-4 h-4 animate-spin" />
      ) : (
        <>
          {Icon && <Icon className="w-4 h-4" />}
          <span>{children}</span>
          {IconRight && <IconRight className="w-4 h-4" />}
        </>
      )}
    </motion.button>
  )
}
