import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Button } from '../ui/Button'

export function CTASection() {
  return (
    <section className="min-h-screen bg-ink text-paper flex items-center justify-center px-6">
      <motion.div
        initial={{ opacity: 0, y: 24 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="text-center max-w-[900px]"
      >
        <h2 className="display-serif text-4xl md:text-[7rem] leading-[0.95] tracking-[-0.03em] text-paper">
          Готовы начать?
        </h2>
        <p className="mt-6 text-lg text-paper/60 max-w-[500px] mx-auto">
          Бесплатно, без кредитной карты, без лишних вопросов.
        </p>
        <div className="mt-10 flex items-center justify-center gap-4">
          <Link to="/register">
            <Button variant="secondary" size="lg" className="bg-paper text-ink border-paper hover:bg-ink hover:text-paper hover:border-paper">
              Создать аккаунт бесплатно
            </Button>
          </Link>
        </div>
      </motion.div>
    </section>
  )
}
