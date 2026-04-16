import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion, useScroll, useMotionValueEvent } from 'framer-motion'
import { cn } from '../../utils/cn'
import { Button } from '../ui/Button'

export function LandingNavbar() {
  const { scrollY } = useScroll()
  const [scrolled, setScrolled] = useState(false)
  const [hidden, setHidden] = useState(false)
  const [lastY, setLastY] = useState(0)

  useMotionValueEvent(scrollY, 'change', (y) => {
    setScrolled(y > 80)
    if (y > lastY && y > 120) setHidden(true)
    else if (y < lastY) setHidden(false)
    setLastY(y)
  })

  return (
    <motion.nav
      animate={{ y: hidden ? '-100%' : '0%' }}
      transition={{ duration: 0.3, ease: [0.76, 0, 0.24, 1] }}
      className={cn(
        'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
        scrolled ? 'bg-paper/92 backdrop-blur-md border-b border-gray-100' : 'bg-transparent'
      )}
    >
      <div className="max-w-container mx-auto px-6 md:px-12 h-[72px] flex items-center justify-between">
        <Link to="/" className="display-serif text-[2rem] tracking-[-0.02em] leading-none">
          KANBAN
        </Link>
        <div className="hidden md:flex items-center gap-8 mono tracking-[0.1em] text-[0.75rem] uppercase text-gray-600">
          <a href="#features" className="hover:text-ink transition-colors">Features</a>
          <a href="#how" className="hover:text-ink transition-colors">How it works</a>
          <a href="#stats" className="hover:text-ink transition-colors">Stats</a>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/login" className="hidden md:inline-flex mono text-[0.75rem] uppercase tracking-[0.1em] text-gray-600 hover:text-ink transition-colors">
            Sign in
          </Link>
          <Link to="/register"><Button size="sm">Get Started</Button></Link>
        </div>
      </div>
    </motion.nav>
  )
}
