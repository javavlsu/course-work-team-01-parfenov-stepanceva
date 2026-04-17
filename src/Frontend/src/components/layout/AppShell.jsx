import { useEffect } from 'react'
import { Header } from './Header'
import { Sidebar } from './Sidebar'
import { useUiStore } from '../../store/uiStore'

export function AppShell({ breadcrumb, children }) {
  const setSidebarOpen = useUiStore((s) => s.setSidebarOpen)

  useEffect(() => {
    const mql = window.matchMedia('(min-width: 768px)')
    const handler = (e) => { if (e.matches) setSidebarOpen(true) }
    mql.addEventListener('change', handler)
    return () => mql.removeEventListener('change', handler)
  }, [setSidebarOpen])

  return (
    <div className="min-h-screen flex flex-col bg-paper">
      <Header breadcrumb={breadcrumb} />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-y-auto">{children}</main>
      </div>
    </div>
  )
}
