import { useCallback } from 'react'
import { ru as ruDateLocale, enUS as enDateLocale } from 'date-fns/locale'
import ru from './locales/ru'
import en from './locales/en'
import { useI18nStore, SUPPORTED_LOCALES, DEFAULT_LOCALE } from './store'

export { SUPPORTED_LOCALES, DEFAULT_LOCALE } from './store'

const dictionaries = { ru, en }
const dateLocales = { ru: ruDateLocale, en: enDateLocale }

// Достаёт значение по точечному пути из объекта.
function dig(obj, path) {
  return path.split('.').reduce((acc, key) => (acc == null ? acc : acc[key]), obj)
}

// Простая интерполяция {placeholder}.
function interpolate(template, params) {
  if (!params || typeof template !== 'string') return template
  return template.replace(/\{(\w+)\}/g, (_, k) => (params[k] != null ? params[k] : `{${k}}`))
}

function lookup(locale, key) {
  const value = dig(dictionaries[locale], key)
  if (value != null) return value
  // fallback: на дефолтный язык, если ключ потерян
  if (locale !== DEFAULT_LOCALE) {
    const fallback = dig(dictionaries[DEFAULT_LOCALE], key)
    if (fallback != null) return fallback
  }
  return null
}

// Главный хук перевода. Возвращает t/locale/setLocale/dateLocale.
// Использует селектор по locale — компонент перерисуется при смене языка.
export function useTranslation() {
  const locale = useI18nStore((s) => s.locale)
  const setLocale = useI18nStore((s) => s.setLocale)

  const t = useCallback(
    (key, params) => {
      const value = lookup(locale, key)
      if (value == null) return key
      return interpolate(value, params)
    },
    [locale]
  )

  return {
    t,
    locale,
    setLocale,
    dateLocale: dateLocales[locale] || dateLocales[DEFAULT_LOCALE],
  }
}

// Версия без хука — для мест вне React (api/client.js).
export function t(key, params) {
  const locale = useI18nStore.getState().locale
  const value = lookup(locale, key)
  if (value == null) return key
  return interpolate(value, params)
}

// Перевод ошибки по коду из ApiException. Принимает axios-error / response.data / payload.
export function translateError(input, fallbackKey = 'errors.generic') {
  const data = extractData(input)
  const code = data?.code
  if (code) {
    const translated = lookup(useI18nStore.getState().locale, `errors.${code}`)
    if (translated) return translated
  }
  if (data?.message) return data.message
  return t(fallbackKey)
}

export function errorCode(input) {
  return extractData(input)?.code ?? null
}

function extractData(input) {
  if (!input) return null
  if (input.response?.data) return input.response.data
  if (typeof input === 'object' && ('code' in input || 'message' in input)) return input
  return null
}
