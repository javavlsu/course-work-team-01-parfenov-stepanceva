import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'

export const SUPPORTED_LOCALES = ['ru', 'en']
export const DEFAULT_LOCALE = 'ru'

export const useI18nStore = create(
  persist(
    (set) => ({
      locale: DEFAULT_LOCALE,
      setLocale: (locale) => {
        if (SUPPORTED_LOCALES.includes(locale)) {
          set({ locale })
          if (typeof document !== 'undefined') {
            document.documentElement.lang = locale
          }
        }
      },
    }),
    {
      name: 'kanban-locale',
      storage: createJSONStorage(() => localStorage),
    }
  )
)
