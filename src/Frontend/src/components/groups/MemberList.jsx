import { Trash2 } from 'lucide-react'
import { Avatar } from '../ui/Avatar'
import { Badge } from '../ui/Badge'
import { useGroupMembers, useUpdateMemberRole, useRemoveMember } from '../../hooks/useGroups'
import { useAuthStore } from '../../store/authStore'

export function MemberList({ groupId, isAdmin }) {
  const { data: members = [], isLoading } = useGroupMembers(groupId)
  const updateRole = useUpdateMemberRole(groupId)
  const removeMember = useRemoveMember(groupId)
  const currentUser = useAuthStore((s) => s.user)

  if (isLoading) {
    return <div className="skeleton h-40 rounded-lg" />
  }

  return (
    <div className="bg-paper border border-gray-100 rounded-lg overflow-hidden">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-xs mono tracking-[0.1em] uppercase text-gray-400 bg-gray-100/50">
            <th className="text-left px-6 py-3 font-normal">Участник</th>
            <th className="text-left py-3 font-normal">Роль</th>
            {isAdmin && <th className="w-10"></th>}
          </tr>
        </thead>
        <tbody>
          {members.map((m) => {
            const isSelf = m.userId === currentUser?.id
            const userStub = { id: m.userId, username: isSelf ? currentUser.username : `user#${m.userId}` }
            return (
              <tr key={m.userId} className="border-t border-gray-100 hover:bg-gray-100/50 transition-colors">
                <td className="px-6 py-3">
                  <div className="flex items-center gap-3">
                    <Avatar user={userStub} size="sm" />
                    <span>{userStub.username}{isSelf && <span className="text-gray-400 ml-1">(вы)</span>}</span>
                  </div>
                </td>
                <td className="py-3">
                  {isAdmin && !isSelf ? (
                    <select
                      value={m.role}
                      onChange={(e) => updateRole.mutate({ userId: m.userId, role: e.target.value })}
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
                        onClick={() => { if (confirm('Удалить участника?')) removeMember.mutate(m.userId) }}
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
          {members.length === 0 && (
            <tr><td colSpan={isAdmin ? 3 : 2} className="px-6 py-8 text-center text-gray-500">Нет участников</td></tr>
          )}
        </tbody>
      </table>
    </div>
  )
}
