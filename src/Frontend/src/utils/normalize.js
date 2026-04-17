// Adapters: backend DTO <-> frontend shape
// Backend uses: name, avatarUrl, title, position, assignee, priority lowercase, role lowercase.
// Frontend uses: username, avatar, name (for board/column/group), order, assigneeId, priority UPPERCASE, role UPPERCASE.

const API_BASE = import.meta.env.VITE_API_BASE ?? '/kanban/api'

export function normalizeUser(u) {
  if (!u) return null
  return {
    id: u.id,
    username: u.name,
    email: u.email,
    avatar: u.avatarUrl ? `${API_BASE}/files/${u.avatarUrl}` : null,
    createdAt: u.createdAt,
  }
}

export function normalizeGroup(g, members = []) {
  if (!g) return null
  return {
    id: g.id,
    name: g.name,
    description: g.description || '',
    createdAt: g.createdAt,
    members: members.map((m) => ({
      userId: m.userId,
      role: (m.role || 'member').toUpperCase(),
      joinedAt: m.joinedAt,
    })),
  }
}

export function normalizeBoard(b) {
  if (!b) return null
  return {
    id: b.id,
    groupId: b.group?.id,
    groupName: b.group?.name,
    name: b.title,
    description: b.description || '',
    authorId: b.createdBy?.id,
    author: normalizeUser(b.createdBy),
    createdAt: b.createdAt,
    pattern: ((b.id || 0) % 3) + 1,
  }
}

export function normalizeColumn(c) {
  if (!c) return null
  return {
    id: c.id,
    boardId: c.boardId,
    name: c.title,
    order: c.position,
    createdAt: c.createdAt,
  }
}

export function normalizeTask(t) {
  if (!t) return null
  return {
    id: t.id,
    columnId: t.columnId,
    title: t.title,
    description: t.description || '',
    assigneeId: t.assignee?.id || null,
    assignee: normalizeUser(t.assignee),
    order: t.position,
    deadline: t.deadline,
    priority: (t.priority || 'medium').toUpperCase(),
    status: (t.status || 'todo').toUpperCase(),
    createdAt: t.createdAt,
    commentsCount: 0,
    attachmentsCount: 0,
  }
}

export function normalizeComment(c) {
  if (!c) return null
  return {
    id: c.id,
    taskId: c.taskId,
    authorId: c.user?.id,
    author: normalizeUser(c.user),
    text: c.text,
    createdAt: c.createdAt,
    updatedAt: c.updatedAt,
  }
}

export function normalizeInvitation(inv) {
  if (!inv) return null
  return {
    id: inv.id,
    group: inv.group ? { id: inv.group.id, name: inv.group.name, description: inv.group.description } : null,
    groupId: inv.group?.id,
    email: inv.email,
    type: inv.type,
    status: inv.status,
    inviteToken: inv.inviteToken,
    createdBy: normalizeUser(inv.createdBy),
    createdAt: inv.createdAt,
    expiresAt: inv.expiresAt,
  }
}

// Priority/status helpers
export const priorityToApi = (p) => (p || 'medium').toLowerCase()
export const statusToApi = (s) => (s || 'todo').toLowerCase().replace('in_progress', 'in_progress')
export const roleToApi = (r) => (r || 'member').toLowerCase()
