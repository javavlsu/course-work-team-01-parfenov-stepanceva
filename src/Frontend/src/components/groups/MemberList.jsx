import { Trash2 } from 'lucide-react'
import { Avatar } from '../ui/Avatar'
import { Badge } from '../ui/Badge'
import { useMockStore } from '../../store/mockStore'
import { formatDate } from '../../utils/dates'
import { toast } from 'sonner'

export function MemberList({ groupId, isAdmin }) {
  const group = useMockStore((s) => s.groups[groupId])
  const users = useMockStore((s) => s.users)
  const updateMemberRole = useMockStore((s) => s.updateMemberRole)
  const removeMember = useMockStore((s) => s.removeMember)
  const currentUserId = useMockStore((s) => s.currentUser.id)

  if (!group) return null

  return (
    <div className="bg-paper border border-gray-100 rounded-lg overflow-hidden">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-xs mono tracking-[0.1em] uppercase text-gray-400 bg-gray-100/50">
            <th className="text-left px-6 py-3 font-normal">Участник</th>
            <th className="text-left py-3 font-normal">Email</th>
            <th className="text-left py-3 font-normal">Роль</th>
            {isAdmin && <th className="w-10"></th>}
          </tr>
        </thead>
        <tbody>
          {group.members.map((m) => {
            const u = users[m.userId]
            if (!u) return null
            const isSelf = u.id === currentUserId
            return (
              <tr key={m.userId} className="border-t border-gray-100 hover:bg-gray-100/50 transition-colors">
                <td className="px-6 py-3">
                  <div className="flex items-center gap-3">
                    <Avatar user={u} size="sm" />
                    <span>{u.username}{isSelf && <span className="text-gray-400 ml-1">(вы)</span>}</span>
                  </div>
                </td>
                <td className="py-3 text-gray-600">{u.email}</td>
                <td className="py-3">
                  {isAdmin && !isSelf ? (
                    <select
                      value={m.role}
                      onChange={(e) => { updateMemberRole(groupId, m.userId, e.target.value); toast.success('Роль обновлена') }}
                      className="bg-transparent text-sm border border-gray-200 rounded px-2 py-1 focus:border-ink outline-none"
                    >
                      <option value="ADMIN">ADMIN</option>
                      <option value="MEMBER">MEMBER</option>
                    </select>
                  ) : (
                    <Badge variant={m.role === 'ADMIN' ? 'ink' : 'default'}>{m.role}</Badge>
                  )}
                </td>
                {isAdmin && (
                  <td className="py-3 pr-4 text-right">
                    {!isSelf && (
                      <button
                        onClick={() => { if (confirm('Удалить участника?')) { removeMember(groupId, m.userId); toast.success('Удалён') } }}
                        className="w-8 h-8 inline-flex items-center justify-center rounded-md text-gray-400 hover:text-priority-high hover:bg-priority-high/10 transition-colors"
                        aria-label="Удалить"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </td>
                )}
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
