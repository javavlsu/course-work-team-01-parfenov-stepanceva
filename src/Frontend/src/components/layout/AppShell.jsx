import { Header } from './Header'
import { Sidebar } from './Sidebar'

export function AppShell({ breadcrumb, children }) {
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
