import { motion } from 'framer-motion'

const steps = [
  { n: '❶', title: 'Создайте группу', desc: 'Назовите свою команду и добавьте описание.' },
  { n: '❷', title: 'Добавьте доску', desc: 'Для каждого проекта или спринта.' },
  { n: '❸', title: 'Пригласите команду', desc: 'По email или публичной ссылке.' },
  { n: '❹', title: 'Работайте', desc: 'Задачи, приоритеты, дедлайны, комментарии.' },
]

export function HowItWorksSection() {
  return (
    <section id="how" className="py-24 px-6 md:px-12 bg-gray-100/50">
      <div className="max-w-container mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
          className="mb-16 text-center"
        >
          <div className="mono tracking-[0.2em] text-xs text-gray-400 uppercase mb-3">Getting started</div>
          <h2 className="display-serif text-3xl md:text-4xl">Как это работает.</h2>
        </motion.div>

        <div className="grid md:grid-cols-4 gap-6">
          {steps.map((s, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: i * 0.1, ease: [0.16, 1, 0.3, 1] }}
              className="relative"
            >
              <div className="display-serif text-4xl text-ink mb-3">{s.n}</div>
              <h3 className="display-serif text-xl mb-2">{s.title}</h3>
              <p className="text-sm text-gray-600">{s.desc}</p>
              {i < steps.length - 1 && (
                <svg className="hidden md:block absolute top-6 right-0 translate-x-1/2 w-8 h-3 text-gray-400" viewBox="0 0 40 12" fill="none">
                  <motion.path
                    d="M0 6 L36 6 M30 1 L36 6 L30 11"
                    stroke="currentColor"
                    strokeWidth="1"
                    strokeLinecap="round"
                    initial={{ pathLength: 0 }}
                    whileInView={{ pathLength: 1 }}
                    viewport={{ once: true }}
                    transition={{ duration: 0.8, delay: 0.3 + i * 0.1 }}
                  />
                </svg>
              )}
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  )
}
