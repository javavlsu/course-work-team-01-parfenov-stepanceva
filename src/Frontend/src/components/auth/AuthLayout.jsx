import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'

export function AuthLayout({ title, subtitle, children, quote }) {
  return (
    <div className="min-h-screen grid md:grid-cols-2 bg-paper">
      <motion.div
        initial={{ opacity: 0, x: -40 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.6, ease: [0.16, 1, 0.3, 1] }}
        className="hidden md:flex relative bg-ink text-paper overflow-hidden p-12 flex-col justify-between"
      >
        <Link to="/" className="display-serif text-xl tracking-tight relative z-10">KANBAN</Link>
        <div
          aria-hidden
          className="absolute inset-0 flex items-center justify-center select-none pointer-events-none"
          style={{ transform: 'rotate(-3deg)' }}
        >
          <span className="display-serif text-hero text-paper opacity-[0.08] leading-none whitespace-nowrap">
            KANBAN
          </span>
        </div>
        <div className="relative z-10 max-w-md">
          <p className="display-serif text-2xl leading-snug text-paper">
            {quote || '"Good design is obvious. Great design is transparent."'}
          </p>
          <p className="mt-4 text-sm text-paper/50 mono">— Joe Sparano</p>
        </div>
      </motion.div>
      <motion.div
        initial={{ opacity: 0, x: 40 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.6, delay: 0.1, ease: [0.16, 1, 0.3, 1] }}
        className="flex items-center justify-center p-8 md:p-12"
      >
        <div className="w-full max-w-[360px]">
          <Link to="/" className="display-serif text-xl md:hidden mb-8 inline-block">KANBAN</Link>
          <div className="mono text-xs tracking-[0.15em] uppercase text-gray-400 mb-3">{subtitle}</div>
          <h1 className="display-serif text-3xl mb-10">{title}</h1>
          {children}
        </div>
      </motion.div>
    </div>
  )
}
