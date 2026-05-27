import { motion } from 'framer-motion'
import { useTranslation } from '../../i18n'

export function FeaturesSection() {
  const { t } = useTranslation()
  const features = [
    { num: '01', title: t('landing.feature1Title'), desc: t('landing.feature1Desc') },
    { num: '02', title: t('landing.feature2Title'), desc: t('landing.feature2Desc') },
    { num: '03', title: t('landing.feature3Title'), desc: t('landing.feature3Desc') },
  ]

  return (
    <section id="features" className="py-24 px-6 md:px-12 border-t border-gray-100">
      <div className="max-w-container mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: '-100px' }}
          transition={{ duration: 0.6 }}
          className="mb-16 text-center"
        >
          <div className="mono tracking-[0.2em] text-xs text-gray-400 uppercase mb-3">{t('landing.featuresTitle')}</div>
          <h2 className="display-serif text-3xl md:text-4xl tracking-[-0.02em]">{t('landing.featuresTitle')}</h2>
        </motion.div>
        <div className="grid md:grid-cols-3 gap-8">
          {features.map((f, i) => (
            <motion.div
              key={f.num}
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: '-80px' }}
              transition={{ duration: 0.5, delay: i * 0.1, ease: [0.16, 1, 0.3, 1] }}
              className="group relative p-8 border border-gray-100 rounded-lg overflow-hidden bg-paper"
            >
              <div className="mono text-xs text-gray-400 mb-6">[{f.num}]</div>
              <h3 className="display-serif text-2xl mb-3">{f.title}</h3>
              <p className="text-base text-gray-600 leading-relaxed">{f.desc}</p>
              <span className="absolute bottom-0 left-0 h-[2px] w-0 bg-ink transition-[width] duration-[300ms] ease-out group-hover:w-full" />
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  )
}
