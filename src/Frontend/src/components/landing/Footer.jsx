import { useTranslation } from '../../i18n'

export function Footer() {
  const { t } = useTranslation()
  return (
    <footer className="border-t border-gray-100 py-10 px-6 md:px-12">
      <div className="max-w-container mx-auto flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div className="display-serif text-2xl">KANBAN</div>
        <div className="text-xs text-gray-400 mono">
          {t('landing.footerCopyright', { year: new Date().getFullYear() })}
        </div>
      </div>
    </footer>
  )
}
