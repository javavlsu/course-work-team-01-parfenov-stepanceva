import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { cn } from '../../utils/cn'

export function Tooltip({ content, children, side = 'top', className }) {
  const [open, setOpen] = useState(false)
  if (!content) return children
  return (
    <span
      className={cn('relative inline-flex', className)}
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
      onFocus={() => setOpen(true)}
      onBlur={() => setOpen(false)}
    >
      {children}
      <AnimatePresence>
        {open && (
          <motion.span
            initial={{ opacity: 0, y: side === 'top' ? 4 : -4 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: side === 'top' ? 4 : -4 }}
            transition={{ duration: 0.15 }}
            className={cn(
              'pointer-events-none absolute z-50 px-2 py-1 rounded-sm bg-ink text-paper text-xs whitespace-nowrap',
              side === 'top' && 'bottom-full mb-2 left-1/2 -translate-x-1/2',
              side === 'bottom' && 'top-full mt-2 left-1/2 -translate-x-1/2',
              side === 'left' && 'right-full mr-2 top-1/2 -translate-y-1/2',
              side === 'right' && 'left-full ml-2 top-1/2 -translate-y-1/2'
            )}
          >
            {content}
          </motion.span>
        )}
      </AnimatePresence>
    </span>
  )
}
