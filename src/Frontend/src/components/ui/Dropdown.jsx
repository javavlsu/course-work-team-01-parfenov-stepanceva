import { useEffect, useRef, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { cn } from '../../utils/cn'

export function Dropdown({ trigger, items, align = 'left', className, menuClassName }) {
  const [open, setOpen] = useState(false)
  const ref = useRef(null)

  useEffect(() => {
    if (!open) return
    const h = (e) => { if (ref.current && !ref.current.contains(e.target)) setOpen(false) }
    document.addEventListener('mousedown', h)
    return () => document.removeEventListener('mousedown', h)
  }, [open])

  return (
    <div ref={ref} className={cn('relative inline-block', className)}>
      <div onClick={() => setOpen((o) => !o)}>{trigger}</div>
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -6, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -6, scale: 0.97 }}
            transition={{ duration: 0.18, ease: [0.16, 1, 0.3, 1] }}
            className={cn(
              'absolute z-50 mt-2 min-w-[180px] bg-paper rounded-md shadow-lg border border-gray-100 p-1',
              align === 'right' ? 'right-0' : 'left-0',
              menuClassName
            )}
          >
            {items.map((it, i) => {
              if (it.divider) return <div key={i} className="h-px bg-gray-100 my-1" />
              return (
                <button
                  key={i}
                  onClick={() => { setOpen(false); it.onClick?.() }}
                  className={cn(
                    'w-full text-left px-3 py-2 text-sm rounded-sm hover:bg-gray-100 transition-colors duration-fast flex items-center gap-2',
                    it.danger && 'text-priority-high hover:bg-priority-high/10',
                    it.disabled && 'opacity-50 pointer-events-none'
                  )}
                >
                  {it.icon && <it.icon className="w-4 h-4" />}
                  <span className="flex-1">{it.label}</span>
                  {it.hint && <span className="text-xs text-gray-400">{it.hint}</span>}
                </button>
              )
            })}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
