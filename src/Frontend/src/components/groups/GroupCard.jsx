import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowRight, Users, LayoutGrid } from 'lucide-react'
import { Badge } from '../ui/Badge'
import { AvatarGroup } from '../ui/Avatar'
import { useGroupMembers } from '../../hooks/useGroups'
import { useMyBoards } from '../../hooks/useBoards'
import { useAuthStore } from '../../store/authStore'

export function GroupCard({ group, index = 0 }) {
  const { data: members = [] } = useGroupMembers(group.id)
  const { data: boards = [] } = useMyBoards()
  const currentUser = useAuthStore((s) => s.user)

  const boardsCount = boards.filter((b) => b.groupId === group.id).length
  const myRole = members.find((m) => m.userId === currentUser?.id)?.role || 'MEMBER'
  const memberUsers = members.map((m) => ({ id: m.userId, username: `#${m.userId}` }))

  return (
    <motion.div
      initial={{ opacity: 0, y: 20, scale: 0.98 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ duration: 0.4, delay: index * 0.06, ease: [0.16, 1, 0.3, 1] }}
      whileHover={{ y: -4 }}
    >
      <Link
        to={`/groups/${group.id}`}
        className="group block bg-paper border border-gray-100 rounded-lg p-6 hover:border-gray-400 hover:shadow-lg transition-all duration-base ease-out"
      >
        <div className="flex items-start justify-between mb-4">
          <Badge variant={myRole === 'ADMIN' ? 'ink' : 'default'}>{myRole}</Badge>
        </div>
        <h3 className="display-serif text-xl mb-1">{group.name}</h3>
        {group.description && <p className="text-sm text-gray-600 line-clamp-2 break-words mb-6">{group.description}</p>}
        <div className="flex items-center gap-4 text-xs text-gray-600 mb-5">
          <span className="inline-flex items-center gap-1.5"><Users className="w-3.5 h-3.5" />{members.length}</span>
          <span className="inline-flex items-center gap-1.5"><LayoutGrid className="w-3.5 h-3.5" />{boardsCount}</span>
        </div>
        <div className="flex items-center justify-between">
          <AvatarGroup users={memberUsers} max={4} size="xs" />
          <span className="inline-flex items-center gap-1 text-sm text-gray-600 opacity-0 group-hover:opacity-100 translate-x-[-4px] group-hover:translate-x-0 transition-all duration-base">
            Открыть <ArrowRight className="w-3.5 h-3.5" />
          </span>
        </div>
      </Link>
    </motion.div>
  )
}
