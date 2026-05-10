import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { LayoutGrid, ChevronRight, User as UserIcon, LogOut, FolderKanban } from 'lucide-react'
import { cn } from '../../utils/cn'
import { useUiStore } from '../../store/uiStore'
import { useGroups } from '../../hooks/useGroups'
import { useMyBoards } from '../../hooks/useBoards'
import { useLogout } from '../../hooks/useAuth'
import { useTranslation } from '../../i18n'

export function Sidebar() {
  const location = useLocation()
  const { data: groups = [] } = useGroups()
  const { data: boards = [] } = useMyBoards()
  const sidebarOpen = useUiStore((s) => s.sidebarOpen)
  const logout = useLogout()
  const { t } = useTranslation()
  const [expanded, setExpanded] = useState({})

  const isActive = (path) => location.pathname === path || location.pathname.startsWith(path + '/')

  return (
    <AnimatePresence initial={false}>
      {sidebarOpen && (
        <motion.aside
          initial={{ width: 0, opacity: 0 }}
          animate={{ width: 260, opacity: 1 }}
          exit={{ width: 0, opacity: 0 }}
          transition={{ duration: 0.3, ease: [0.76, 0, 0.24, 1] }}
          className="shrink-0 overflow-hidden bg-gray-100/70 border-r border-gray-100"
        >
          <div className="w-[260px] h-[calc(100vh-64px)] flex flex-col">
            <div className="px-3 py-4 flex-1 overflow-y-auto">
              <SidebarItem
                to="/dashboard"
                icon={LayoutGrid}
                label={t('nav.dashboard')}
                active={isActive('/dashboard') && !location.pathname.startsWith('/groups') && !location.pathname.startsWith('/boards')}
              />

              <div className="mt-6 mb-2 px-3 text-xs mono tracking-[0.15em] text-gray-400 uppercase">{t('dashboard.myGroups')}</div>

              {groups.length === 0 && (
                <div className="px-3 py-2 text-sm text-gray-400">{t('dashboard.noGroups')}</div>
              )}

              {groups.map((g) => {
                const isOpen = !!expanded[g.id]
                const groupBoards = boards.filter((b) => b.groupId === g.id)
                return (
                  <div key={g.id} className="mb-1">
                    <div className="flex items-stretch">
                      <button
                        onClick={() => setExpanded((e) => ({ ...e, [g.id]: !e[g.id] }))}
                        className="w-6 flex items-center justify-center text-gray-400 hover:text-ink"
                        aria-label={t('common.more')}
                      >
                        <ChevronRight
                          className={cn('w-3.5 h-3.5 transition-transform duration-base', isOpen && 'rotate-90')}
                        />
                      </button>
                      <SidebarItem
                        to={`/groups/${g.id}`}
                        label={g.name}
                        active={location.pathname === `/groups/${g.id}`}
                        compact
                        className="flex-1"
                      />
                    </div>
                    <AnimatePresence initial={false}>
                      {isOpen && (
                        <motion.div
                          initial={{ height: 0, opacity: 0 }}
                          animate={{ height: 'auto', opacity: 1 }}
                          exit={{ height: 0, opacity: 0 }}
                          transition={{ duration: 0.2 }}
                          className="overflow-hidden pl-7"
                        >
                          {groupBoards.map((b) => (
                            <SidebarItem
                              key={b.id}
                              to={`/boards/${b.id}`}
                              label={b.name}
                              icon={FolderKanban}
                              active={location.pathname === `/boards/${b.id}`}
                              compact
                              small
                            />
                          ))}
                          {groupBoards.length === 0 && (
                            <div className="text-xs text-gray-400 px-3 py-1">{t('dashboard.noBoards')}</div>
                          )}
                        </motion.div>
                      )}
                    </AnimatePresence>
                  </div>
                )
              })}
            </div>

            <div className="p-3 border-t border-gray-200/60">
              <SidebarItem to="/profile" icon={UserIcon} label={t('nav.profile')} active={isActive('/profile')} />
              <button
                onClick={() => logout.mutate()}
                className="w-full text-left px-3 py-2 h-9 rounded-md text-sm text-gray-600 hover:text-ink hover:bg-gray-100 transition-all duration-base flex items-center gap-2"
              >
                <LogOut className="w-4 h-4" />
                <span>{t('nav.logout')}</span>
              </button>
            </div>
          </div>
        </motion.aside>
      )}
    </AnimatePresence>
  )
}

function SidebarItem({ to, icon: Icon, label, active, compact = false, small = false, className }) {
  return (
    <Link
      to={to}
      className={cn(
        'group/item flex items-center gap-2 h-9 px-3 rounded-md text-sm transition-all duration-base',
        'text-gray-600 hover:text-ink',
        active ? 'bg-paper shadow-sm text-ink font-medium' : 'hover:bg-paper/70',
        small && 'h-8 text-[13px]',
        className
      )}
    >
      {Icon && <Icon className="w-4 h-4 shrink-0 transition-transform duration-fast group-hover/item:translate-x-0.5" />}
      <span className="truncate transition-transform duration-fast group-hover/item:translate-x-0.5">{label}</span>
    </Link>
  )
}
