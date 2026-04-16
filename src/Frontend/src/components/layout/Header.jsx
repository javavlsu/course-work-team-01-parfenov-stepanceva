import { useNavigate, Link, useLocation } from 'react-router-dom'
import { Bell, LogOut, User as UserIcon, Menu } from 'lucide-react'
import { Avatar } from '../ui/Avatar'
import { Badge } from '../ui/Badge'
import { Dropdown } from '../ui/Dropdown'
import { useMockStore } from '../../store/mockStore'
import { useUiStore } from '../../store/uiStore'
import { useAuthStore } from '../../store/authStore'

export function Header({ breadcrumb }) {
  const navigate = useNavigate()
  const location = useLocation()
  const currentUser = useMockStore((s) => s.currentUser)
  const toggleSidebar = useUiStore((s) => s.toggleSidebar)
  const logout = useAuthStore((s) => s.logout)

  return (
    <header className="h-16 px-6 flex items-center justify-between border-b border-gray-100 bg-paper/95 backdrop-blur-sm sticky top-0 z-30">
      <div className="flex items-center gap-3">
        <button
          onClick={toggleSidebar}
          aria-label="Toggle sidebar"
          className="md:hidden w-9 h-9 inline-flex items-center justify-center rounded-md hover:bg-gray-100"
        >
          <Menu className="w-5 h-5" />
        </button>
        <Link to="/dashboard" className="display-serif text-xl tracking-tight">KANBAN</Link>
        {breadcrumb?.length > 0 && (
          <nav className="hidden md:flex items-center gap-1 ml-4 text-sm text-gray-400">
            <span className="mx-1">/</span>
            {breadcrumb.map((b, i) => (
              <span key={i} className="flex items-center gap-1">
                {b.to ? <Link to={b.to} className="hover:text-ink transition-colors">{b.label}</Link> : <span className="text-ink">{b.label}</span>}
                {i < breadcrumb.length - 1 && <span className="mx-1">›</span>}
              </span>
            ))}
          </nav>
        )}
      </div>

      <div className="flex items-center gap-2">
        <button aria-label="Уведомления" className="relative w-9 h-9 inline-flex items-center justify-center rounded-md hover:bg-gray-100 transition-colors">
          <Bell className="w-5 h-5" />
          <Badge variant="danger" className="absolute -top-1 -right-1 !px-1.5 !py-0 text-[10px]">2</Badge>
        </button>
        <Dropdown
          align="right"
          trigger={
            <button className="inline-flex items-center gap-2 p-1 rounded-full hover:bg-gray-100 transition-colors">
              <Avatar user={currentUser} size="md" />
            </button>
          }
          items={[
            { label: currentUser?.username, hint: currentUser?.email, disabled: true },
            { divider: true },
            { label: 'Профиль', icon: UserIcon, onClick: () => navigate('/profile') },
            { divider: true },
            { label: 'Выйти', icon: LogOut, onClick: () => { logout(); navigate('/login') }, danger: true },
          ]}
        />
      </div>
    </header>
  )
}
