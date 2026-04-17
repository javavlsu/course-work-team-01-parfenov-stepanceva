import { useEffect, useRef, useState } from 'react'
import { motion, useInView } from 'framer-motion'

function CountUp({ to, suffix = '', duration = 1.5 }) {
  const [val, setVal] = useState(0)
  const ref = useRef(null)
  const inView = useInView(ref, { once: true, margin: '-80px' })
  useEffect(() => {
    if (!inView) return
    let start = null
    const step = (ts) => {
      if (!start) start = ts
      const p = Math.min(1, (ts - start) / (duration * 1000))
      const eased = 1 - Math.pow(1 - p, 3)
      setVal(Math.round(to * eased))
      if (p < 1) requestAnimationFrame(step)
    }
    requestAnimationFrame(step)
  }, [inView, to, duration])
  return <span ref={ref}>{val.toLocaleString('ru-RU')}{suffix}</span>
}

export function StatsSection() {
  return (
    <section id="stats" className="py-24 px-6 md:px-12 border-t border-gray-100">
      <div className="max-w-container mx-auto grid md:grid-cols-3 gap-12">
        {[
          { n: 1200, suffix: '+', label: 'пользователей' },
          { n: 48, suffix: '', label: 'часа до запуска' },
          { n: 12, suffix: '', label: 'интеграций' },
        ].map((s, i) => (
          <motion.div
            key={i}
            initial={{ opacity: 0, y: 24 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: i * 0.1 }}
          >
            <div className="display-serif text-4xl md:text-[5rem] font-bold leading-none tracking-tight">
              <CountUp to={s.n} suffix={s.suffix} />
            </div>
            <div className="mt-3 text-sm text-gray-600">{s.label}</div>
          </motion.div>
        ))}
      </div>
    </section>
  )
}
