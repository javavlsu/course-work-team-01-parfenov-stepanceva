import { Globe, Check } from 'lucide-react'
import { Dropdown } from './Dropdown'
import { useTranslation, SUPPORTED_LOCALES } from '../../i18n'

export function LanguageSwitcher({ align = 'right', size = 'md' }) {
  const { locale, setLocale, t } = useTranslation()

  const dimensions = size === 'sm' ? 'h-8 px-2.5 text-xs' : 'h-9 px-3 text-sm'

  return (
    <Dropdown
      align={align}
      trigger={
        <button
          className={`inline-flex items-center gap-1.5 rounded-md border border-gray-200 hover:border-ink transition-colors ${dimensions}`}
          aria-label={t('common.language')}
        >
          <Globe className="w-4 h-4" />
          <span className="mono tracking-[0.08em] uppercase">{t(`language.short.${locale}`)}</span>
        </button>
      }
      items={SUPPORTED_LOCALES.map((code) => ({
        label: t(`language.${code}`),
        icon: locale === code ? Check : undefined,
        onClick: () => setLocale(code),
      }))}
    />
  )
}
