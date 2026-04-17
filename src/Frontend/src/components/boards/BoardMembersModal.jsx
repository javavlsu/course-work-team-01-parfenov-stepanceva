import { UserPlus, UserMinus } from 'lucide-react'
import { Modal } from '../ui/Modal'
import { Avatar } from '../ui/Avatar'
import { Badge } from '../ui/Badge'
import { useBoardUsers, useAddBoardUser, useRemoveBoardUser } from '../../hooks/useBoards'
import { useGroupMembers } from '../../hooks/useGroups'
import { useAuthStore } from '../../store/authStore'

export function BoardMembersModal({ open, onClose, boardId, groupId }) {
  const currentUser = useAuthStore((s) => s.user)
  const { data: boardUsers = [] } = useBoardUsers(boardId)
  const { data: groupMembers = [] } = useGroupMembers(groupId)
  const addUser = useAddBoardUser(boardId)
  const removeUser = useRemoveBoardUser(boardId)

  const myGroupRole = groupMembers.find((m) => m.userId === currentUser?.id)?.role
  const isAdmin = myGroupRole === 'ADMIN'

  const boardUserIds = new Set(boardUsers.map((u) => u.id))
  const notOnBoard = groupMembers.filter((m) => !boardUserIds.has(m.userId))

  return (
    <Modal open={open} onClose={onClose} title="Участники доски" size="md">
      <div className="px-6 pb-6 space-y-6">

        <section>
          <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-3">
            На доске ({boardUsers.length})
          </div>
          <div className="space-y-1">
            {boardUsers.map((u) => {
              const isSelf = u.id === currentUser?.id
              return (
                <div key={u.id} className="flex items-center justify-between py-2 px-3 rounded-md hover:bg-gray-100/60">
                  <div className="flex items-center gap-3">
                    <Avatar user={u} size="sm" />
                    <span className="text-sm">{u.username}{isSelf && <span className="text-gray-400 ml-1">(вы)</span>}</span>
                  </div>
                  {isAdmin && !isSelf && (
                    <button
                      onClick={() => { if (confirm(`Убрать ${u.username} с доски?`)) removeUser.mutate(u.id) }}
                      className="w-8 h-8 inline-flex items-center justify-center rounded-md text-gray-400 hover:text-priority-high hover:bg-priority-high/10 transition-colors"
                      aria-label="Убрать с доски"
                    >
                      <UserMinus className="w-4 h-4" />
                    </button>
                  )}
                </div>
              )
            })}
            {boardUsers.length === 0 && (
              <p className="text-sm text-gray-400 py-2">Нет участников</p>
            )}
          </div>
        </section>

        {isAdmin && (
          <section>
            <div className="mono text-[11px] tracking-[0.1em] uppercase text-gray-400 mb-3">
              Добавить из группы
            </div>
            {notOnBoard.length === 0 ? (
              <p className="text-sm text-gray-400 py-2">Все участники группы уже на доске</p>
            ) : (
              <div className="space-y-1">
                {notOnBoard.map((m) => {
                  const u = m.user || { id: m.userId, username: `user#${m.userId}` }
                  return (
                    <div key={m.userId} className="flex items-center justify-between py-2 px-3 rounded-md hover:bg-gray-100/60">
                      <div className="flex items-center gap-3">
                        <Avatar user={u} size="sm" />
                        <div>
                          <span className="text-sm">{u.username}</span>
                          <Badge variant={m.role === 'ADMIN' ? 'ink' : 'default'} className="ml-2">{m.role}</Badge>
                        </div>
                      </div>
                      <button
                        onClick={() => addUser.mutate(m.userId)}
                        disabled={addUser.isPending}
                        className="w-8 h-8 inline-flex items-center justify-center rounded-md text-gray-400 hover:text-ink hover:bg-gray-200 transition-colors disabled:opacity-40"
                        aria-label="Добавить на доску"
                      >
                        <UserPlus className="w-4 h-4" />
                      </button>
                    </div>
                  )
                })}
              </div>
            )}
          </section>
        )}

        {!isAdmin && (
          <p className="text-sm text-gray-400">Только администратор группы может управлять составом доски.</p>
        )}
      </div>
    </Modal>
  )
}
