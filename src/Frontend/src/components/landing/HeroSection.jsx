import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion, useMotionValue, useSpring, useTransform } from 'framer-motion'
import { ArrowRight } from 'lucide-react'
import { Button } from '../ui/Button'

const wordVariants = {
  initial: { clipPath: 'inset(0 100% 0 0)' },
  animate: (i) => ({
    clipPath: 'inset(0 0% 0 0)',
    transition: { duration: 0.8, ease: [0.16, 1, 0.3, 1], delay: 0.1 + i * 0.08 },
  }),
}

export function HeroSection() {
  const mx = useMotionValue(0)
  const my = useMotionValue(0)
  const rx = useSpring(useTransform(my, [-0.5, 0.5], [5, -5]), { stiffness: 120, damping: 15 })
  const ry = useSpring(useTransform(mx, [-0.5, 0.5], [-5, 5]), { stiffness: 120, damping: 15 })

  const onMove = (e) => {
    const r = e.currentTarget.getBoundingClientRect()
    mx.set((e.clientX - r.left) / r.width - 0.5)
    my.set((e.clientY - r.top) / r.height - 0.5)
  }
  const onLeave = () => { mx.set(0); my.set(0) }

  return (
    <section className="relative pt-[140px] pb-[120px] px-6 md:px-12 overflow-hidden">
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 flex items-center justify-center select-none"
      >
        <span className="display-serif text-[28vw] text-gray-100/60 leading-none whitespace-nowrap">
          KANBAN
        </span>
      </div>
      <div className="max-w-container mx-auto relative grid md:grid-cols-5 gap-12 items-center">
        <div className="md:col-span-3">
          <h1 className="display-serif text-hero leading-[0.92] tracking-[-0.03em]">
            {['MANAGE', 'YOUR WORK', 'BEAUTIFULLY.'].map((w, i) => (
              <motion.span
                key={w}
                custom={i}
                variants={wordVariants}
                initial="initial"
                animate="animate"
                className="block"
              >
                {w}
              </motion.span>
            ))}
          </h1>
          <motion.p
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4, duration: 0.5 }}
            className="mt-8 text-lg text-gray-600 max-w-[420px]"
          >
            Kanban для команд, которым важна не только эффективность, но и то, как выглядит процесс.
          </motion.p>
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.55, duration: 0.5 }}
            className="mt-10 flex flex-wrap items-center gap-4"
          >
            <Link to="/register"><Button size="lg">Start for free</Button></Link>
            <a href="#how" className="group inline-flex items-center gap-2 text-sm text-ink hover:text-gray-600 transition-colors">
              See it in action
              <ArrowRight className="w-4 h-4 transition-transform duration-base group-hover:translate-x-1" />
            </a>
          </motion.div>
        </div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 24 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          transition={{ delay: 0.7, duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
          className="md:col-span-2 perspective-[1200px]"
          onMouseMove={onMove}
          onMouseLeave={onLeave}
        >
          <motion.div
            style={{ rotateX: rx, rotateY: ry, transformStyle: 'preserve-3d' }}
            className="relative p-5 bg-paper rounded-xl shadow-xl border border-gray-100"
          >
            <div className="mono text-[10px] tracking-[0.1em] uppercase text-gray-400 mb-3">Sprint 42</div>
            <div className="grid grid-cols-3 gap-2">
              {[
                { col: 'TO DO', items: ['Fix auth bug', 'Profile redesign'] },
                { col: 'IN PROGRESS', items: ['Kanban UI'] },
                { col: 'DONE', items: ['Landing'] },
              ].map((c) => (
                <div key={c.col} className="bg-gray-100 rounded-md p-2">
                  <div className="text-[9px] mono tracking-[0.12em] uppercase text-gray-600 mb-2">{c.col}</div>
                  <div className="space-y-1.5">
                    {c.items.map((t, i) => (
                      <div key={i} className="bg-paper rounded px-2 py-1.5 text-[11px] shadow-sm">
                        <div className="flex items-center gap-1 mb-1">
                          <span className="w-1.5 h-1.5 rounded-full bg-priority-high" />
                          <span className="mono text-[8px] text-gray-400">HIGH</span>
                        </div>
                        <div className="text-ink">{t}</div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        </motion.div>
      </div>
    </section>
  )
}
