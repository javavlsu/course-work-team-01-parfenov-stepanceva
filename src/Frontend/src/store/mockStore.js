import { create } from 'zustand'
import { persist } from 'zustand/middleware'

const uid = () => Math.random().toString(36).slice(2, 10)

const seed = () => {
  const userId = 'me'
  const u2 = uid(), u3 = uid()
  const g1 = uid(), g2 = uid()
  const b1 = uid(), b2 = uid()
  const cTodo = uid(), cProg = uid(), cDone = uid()
  return {
    currentUser: { id: userId, username: 'Roman', email: 'paromanprog@gmail.com', avatar: null, createdAt: '2024-01-01T10:00:00Z' },
    users: {
      [userId]: { id: userId, username: 'Roman', email: 'paromanprog@gmail.com', avatar: null },
      [u2]: { id: u2, username: 'Anna K.', email: 'anna@ex.com', avatar: null },
      [u3]: { id: u3, username: 'Max P.', email: 'max@ex.com', avatar: null },
    },
    groups: {
      [g1]: { id: g1, name: 'Team Alpha', description: 'Дизайн и фронтенд', members: [
        { userId, role: 'ADMIN' }, { userId: u2, role: 'MEMBER' }, { userId: u3, role: 'MEMBER' },
      ], createdAt: '2024-03-01T10:00:00Z' },
      [g2]: { id: g2, name: 'Marketing', description: 'Запуск кампаний', members: [
        { userId, role: 'MEMBER' }, { userId: u2, role: 'ADMIN' },
      ], createdAt: '2024-04-10T10:00:00Z' },
    },
    boards: {
      [b1]: { id: b1, groupId: g1, name: 'Sprint 42', description: 'Q2 спринт', authorId: userId, createdAt: '2024-05-12T10:00:00Z', pattern: 1 },
      [b2]: { id: b2, groupId: g1, name: 'Roadmap', description: 'Дорожная карта', authorId: u2, createdAt: '2024-05-20T10:00:00Z', pattern: 3 },
    },
    columns: {
      [cTodo]: { id: cTodo, boardId: b1, name: 'TO DO', order: 0 },
      [cProg]: { id: cProg, boardId: b1, name: 'IN PROGRESS', order: 1 },
      [cDone]: { id: cDone, boardId: b1, name: 'DONE', order: 2 },
    },
    tasks: (() => {
      const t = {}
      const mk = (columnId, title, priority, i, desc = '') => {
        const id = uid()
        t[id] = {
          id, columnId, title, description: desc,
          priority, status: 'TODO',
          assigneeId: [userId, u2, u3][i % 3],
          deadline: new Date(Date.now() + (i + 2) * 86400000 * (i % 2 === 0 ? 1 : 3)).toISOString(),
          order: i,
          commentsCount: i,
          attachmentsCount: i % 2,
          createdAt: new Date().toISOString(),
        }
      }
      mk(cTodo, 'Исправить баг с авторизацией', 'HIGH', 0, 'Пользователь не может войти при использовании специальных символов в пароле.')
      mk(cTodo, 'Обновить дизайн профиля', 'MEDIUM', 1)
      mk(cTodo, 'Добавить экспорт досок', 'LOW', 2)
      mk(cTodo, 'Настроить CI/CD', 'MEDIUM', 3)
      mk(cProg, 'Редизайн Kanban колонок', 'HIGH', 4, 'Editorial layout, крупная типографика.')
      mk(cProg, 'Интеграция drag & drop', 'MEDIUM', 5)
      mk(cDone, 'Landing page', 'LOW', 6)
      mk(cDone, 'Настройка Tailwind', 'LOW', 7)
      return t
    })(),
    comments: {},
    invitations: {},
  }
}

export const useMockStore = create(
  persist(
    (set, get) => ({
      ...seed(),

      reset: () => set(seed()),

      // user
      updateUser: (patch) => set((s) => ({
        currentUser: { ...s.currentUser, ...patch },
        users: { ...s.users, [s.currentUser.id]: { ...s.users[s.currentUser.id], ...patch } },
      })),

      // groups
      listGroups: () => {
        const s = get()
        return Object.values(s.groups).filter((g) => g.members.some((m) => m.userId === s.currentUser.id))
      },
      createGroup: (data) => {
        const id = uid()
        const group = { id, ...data, members: [{ userId: get().currentUser.id, role: 'ADMIN' }], createdAt: new Date().toISOString() }
        set((s) => ({ groups: { ...s.groups, [id]: group } }))
        return group
      },
      updateGroup: (id, patch) => set((s) => ({ groups: { ...s.groups, [id]: { ...s.groups[id], ...patch } } })),
      removeGroup: (id) => set((s) => {
        const { [id]: _, ...rest } = s.groups
        return { groups: rest }
      }),
      removeMember: (groupId, userId) => set((s) => ({
        groups: { ...s.groups, [groupId]: { ...s.groups[groupId], members: s.groups[groupId].members.filter((m) => m.userId !== userId) } },
      })),
      updateMemberRole: (groupId, userId, role) => set((s) => ({
        groups: { ...s.groups, [groupId]: { ...s.groups[groupId], members: s.groups[groupId].members.map((m) => m.userId === userId ? { ...m, role } : m) } },
      })),

      // boards
      listBoards: (groupId) => Object.values(get().boards).filter((b) => b.groupId === groupId),
      createBoard: (groupId, data) => {
        const id = uid()
        const board = { id, groupId, ...data, authorId: get().currentUser.id, createdAt: new Date().toISOString(), pattern: Math.floor(Math.random() * 3) + 1 }
        set((s) => ({ boards: { ...s.boards, [id]: board } }))
        // default columns
        const cols = ['TO DO', 'IN PROGRESS', 'DONE']
        cols.forEach((name, i) => {
          const cid = uid()
          set((s) => ({ columns: { ...s.columns, [cid]: { id: cid, boardId: id, name, order: i } } }))
        })
        return board
      },
      updateBoard: (id, patch) => set((s) => ({ boards: { ...s.boards, [id]: { ...s.boards[id], ...patch } } })),
      removeBoard: (id) => set((s) => {
        const { [id]: _, ...rest } = s.boards
        return { boards: rest }
      }),

      // columns
      listColumns: (boardId) => Object.values(get().columns).filter((c) => c.boardId === boardId).sort((a, b) => a.order - b.order),
      createColumn: (boardId, data) => {
        const id = uid()
        const cols = Object.values(get().columns).filter((c) => c.boardId === boardId)
        const column = { id, boardId, ...data, order: cols.length }
        set((s) => ({ columns: { ...s.columns, [id]: column } }))
        return column
      },
      updateColumn: (id, patch) => set((s) => ({ columns: { ...s.columns, [id]: { ...s.columns[id], ...patch } } })),
      removeColumn: (id) => set((s) => {
        const { [id]: _, ...rest } = s.columns
        // also remove tasks
        const tasks = Object.fromEntries(Object.entries(s.tasks).filter(([, t]) => t.columnId !== id))
        return { columns: rest, tasks }
      }),

      // tasks
      listTasks: (boardId) => {
        const cols = Object.values(get().columns).filter((c) => c.boardId === boardId).map((c) => c.id)
        return Object.values(get().tasks).filter((t) => cols.includes(t.columnId)).sort((a, b) => a.order - b.order)
      },
      getTask: (id) => get().tasks[id],
      createTask: (columnId, data) => {
        const id = uid()
        const tasksInCol = Object.values(get().tasks).filter((t) => t.columnId === columnId)
        const task = {
          id, columnId, title: data.title, description: data.description || '',
          priority: data.priority || 'MEDIUM',
          status: 'TODO',
          assigneeId: data.assigneeId || null,
          deadline: data.deadline || null,
          order: tasksInCol.length,
          commentsCount: 0, attachmentsCount: 0,
          createdAt: new Date().toISOString(),
        }
        set((s) => ({ tasks: { ...s.tasks, [id]: task } }))
        return task
      },
      updateTask: (id, patch) => set((s) => ({ tasks: { ...s.tasks, [id]: { ...s.tasks[id], ...patch } } })),
      moveTask: (id, toColumnId, toIndex) => set((s) => {
        const task = s.tasks[id]
        if (!task) return {}
        const updated = { ...s.tasks }
        // renumber within source column
        const source = Object.values(updated).filter((t) => t.columnId === task.columnId && t.id !== id).sort((a, b) => a.order - b.order)
        source.forEach((t, i) => { updated[t.id] = { ...updated[t.id], order: i } })
        // insert into destination
        const dest = Object.values(updated).filter((t) => t.columnId === toColumnId && t.id !== id).sort((a, b) => a.order - b.order)
        const clampedIdx = Math.max(0, Math.min(toIndex, dest.length))
        dest.splice(clampedIdx, 0, { ...task, columnId: toColumnId })
        dest.forEach((t, i) => { updated[t.id] = { ...updated[t.id], columnId: toColumnId, order: i } })
        return { tasks: updated }
      }),
      removeTask: (id) => set((s) => {
        const { [id]: _, ...rest } = s.tasks
        return { tasks: rest }
      }),

      // comments
      listComments: (taskId) => Object.values(get().comments).filter((c) => c.taskId === taskId).sort((a, b) => a.createdAt.localeCompare(b.createdAt)),
      addComment: (taskId, text) => {
        const id = uid()
        const c = { id, taskId, authorId: get().currentUser.id, text, createdAt: new Date().toISOString() }
        set((s) => ({
          comments: { ...s.comments, [id]: c },
          tasks: { ...s.tasks, [taskId]: { ...s.tasks[taskId], commentsCount: (s.tasks[taskId]?.commentsCount || 0) + 1 } },
        }))
        return c
      },
      removeComment: (id) => set((s) => {
        const c = s.comments[id]
        if (!c) return {}
        const { [id]: _, ...rest } = s.comments
        return {
          comments: rest,
          tasks: { ...s.tasks, [c.taskId]: { ...s.tasks[c.taskId], commentsCount: Math.max(0, (s.tasks[c.taskId]?.commentsCount || 1) - 1) } },
        }
      }),
    }),
    { name: 'kanban-mock' }
  )
)
