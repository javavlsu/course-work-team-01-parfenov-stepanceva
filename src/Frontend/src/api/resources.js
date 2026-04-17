import { client } from './client'
import {
  normalizeUser, normalizeGroup, normalizeBoard, normalizeColumn,
  normalizeTask, normalizeComment, normalizeInvitation, normalizeHistory,
  priorityToApi, statusToApi, roleToApi,
} from '../utils/normalize'

// Users
export const usersApi = {
  me: () => client.get('/users/profile').then((r) => normalizeUser(r.data)),
  updateName: (name) => client.patch('/users/profile/name', { name }).then((r) => normalizeUser(r.data)),
  updatePassword: (oldPassword, newPassword) =>
    client.patch('/users/profile/password', { oldPassword, newPassword }).then((r) => r.data),
  uploadAvatar: (file) => {
    const fd = new FormData()
    fd.append('file', file)
    return client.post('/users/profile/avatar', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => normalizeUser(r.data))
  },
}

// Groups
export const groupsApi = {
  list: () => client.get('/groupteams/').then((r) => r.data.map((g) => normalizeGroup(g))),
  get: (id) => client.get(`/groupteams/${id}`).then((r) => normalizeGroup(r.data)),
  create: (data) => client.post('/groupteams/', data).then((r) => normalizeGroup(r.data)),
  update: (id, data) => client.put(`/groupteams/${id}`, data).then((r) => normalizeGroup(r.data)),
  remove: (id) => client.delete(`/groupteams/${id}`),
}

// Group members
export const membersApi = {
  list: (groupId) => client.get(`/group-members/${groupId}`).then((r) =>
    r.data.map((m) => ({
      groupId: m.groupId,
      userId: m.userId,
      user: normalizeUser(m.user),
      role: (m.role || 'member').toUpperCase(),
      joinedAt: m.joinedAt,
    }))
  ),
  add: (groupId, userId, role = 'member') =>
    client.post(`/group-members/${groupId}`, { userId, role: roleToApi(role) }),
  updateRole: (groupId, userId, role) =>
    client.put(`/group-members/${groupId}/users/${userId}`, { role: roleToApi(role) }),
  remove: (groupId, userId) =>
    client.delete(`/group-members/${groupId}/users/${userId}`),
}

// Invitations
export const invitationsApi = {
  listForGroup: (groupId) =>
    client.get(`/invitations/group/${groupId}`).then((r) => r.data.map(normalizeInvitation)),
  createEmail: (groupId, email, expiresInDays = 7) =>
    client.post(`/invitations/group/${groupId}/email`, { email, expiresInDays }).then((r) => normalizeInvitation(r.data)),
  createLink: (groupId, expiresInDays = 7) =>
    client.post(`/invitations/group/${groupId}/link`, { expiresInDays }).then((r) => normalizeInvitation(r.data)),
  cancel: (invitationId) => client.delete(`/invitations/${invitationId}`),
  my: () => client.get('/invitations/my').then((r) => r.data.map(normalizeInvitation)),
  respond: (invitationId, accept) =>
    client.post(`/invitations/${invitationId}/respond`, { accept }).then((r) => normalizeInvitation(r.data)),
  join: (token) => client.post(`/invitations/join/${token}`).then((r) => normalizeInvitation(r.data)),
}

// Boards
export const boardsApi = {
  all: () => client.get('/boards/').then((r) => r.data.map(normalizeBoard)),
  listForGroup: (groupId) =>
    client.get(`/boards/group/${groupId}`).then((r) => r.data.map(normalizeBoard)),
  get: (id) => client.get(`/boards/${id}`).then((r) => normalizeBoard(r.data)),
  create: (groupId, title, description = '') =>
    client.post('/boards/', { groupId, title, description }).then((r) => normalizeBoard(r.data)),
  update: (id, title, description = '') =>
    client.put(`/boards/${id}`, { title, description }).then((r) => normalizeBoard(r.data)),
  remove: (id) => client.delete(`/boards/${id}`),
}

// Board users
export const boardUsersApi = {
  list: (boardId) => client.get(`/boards/${boardId}/users/`).then((r) => r.data.map(normalizeUser)),
  add: (boardId, userId) => client.post(`/boards/${boardId}/users/${userId}`),
  remove: (boardId, userId) => client.delete(`/boards/${boardId}/users/${userId}`),
}

// Columns
export const columnsApi = {
  list: (boardId) =>
    client.get(`/boards/${boardId}/columns/`).then((r) => r.data.map(normalizeColumn)),
  create: (boardId, title) =>
    client.post(`/boards/${boardId}/columns/`, { title }).then((r) => normalizeColumn(r.data)),
  update: (boardId, columnId, data) =>
    client.put(`/boards/${boardId}/columns/${columnId}`, data).then((r) => normalizeColumn(r.data)),
  move: (boardId, columnId, newPosition) =>
    client.put(`/boards/${boardId}/columns/${columnId}/move`, null, { params: { newPosition } }),
  remove: (boardId, columnId) =>
    client.delete(`/boards/${boardId}/columns/${columnId}`),
}

// Tasks
export const tasksApi = {
  list: (boardId) =>
    client.get(`/boards/${boardId}/tasks/`).then((r) => r.data.map(normalizeTask)),
  get: (boardId, taskId) =>
    client.get(`/boards/${boardId}/tasks/${taskId}`).then((r) => normalizeTask(r.data)),
  listByColumn: (boardId, columnId) =>
    client.get(`/boards/${boardId}/tasks/column/${columnId}`).then((r) => r.data.map(normalizeTask)),
  mine: (boardId) =>
    client.get(`/boards/${boardId}/tasks/my`).then((r) => r.data.map(normalizeTask)),
  create: (boardId, data) => {
    const payload = {
      columnId: data.columnId,
      title: data.title,
      description: data.description || undefined,
      assigneeId: data.assigneeId || undefined,
      deadline: data.deadline || undefined,
      priority: data.priority ? priorityToApi(data.priority) : undefined,
      status: data.status ? statusToApi(data.status) : undefined,
    }
    return client.post(`/boards/${boardId}/tasks/`, payload).then((r) => normalizeTask(r.data))
  },
  update: (boardId, taskId, data) => {
    const payload = {}
    if (data.columnId !== undefined) payload.columnId = data.columnId
    if (data.title !== undefined) payload.title = data.title
    if (data.description !== undefined) payload.description = data.description
    if (data.assigneeId !== undefined) payload.assigneeId = data.assigneeId
    if (data.position !== undefined) payload.position = data.position + 1
    if (data.deadline !== undefined) payload.deadline = data.deadline
    if (data.priority !== undefined) payload.priority = priorityToApi(data.priority)
    if (data.status !== undefined) payload.status = statusToApi(data.status)
    return client.put(`/boards/${boardId}/tasks/${taskId}`, payload).then((r) => normalizeTask(r.data))
  },
  remove: (boardId, taskId) => client.delete(`/boards/${boardId}/tasks/${taskId}`),
}

// Comments
export const commentsApi = {
  list: (boardId, taskId) =>
    client.get(`/boards/${boardId}/tasks/${taskId}/comments`).then((r) => r.data.map(normalizeComment)),
  create: (boardId, taskId, text) =>
    client.post(`/boards/${boardId}/tasks/${taskId}/comments`, { text }).then((r) => normalizeComment(r.data)),
  update: (boardId, taskId, commentId, text) =>
    client.put(`/boards/${boardId}/tasks/${taskId}/comments/${commentId}`, { text }).then((r) => normalizeComment(r.data)),
  remove: (boardId, taskId, commentId) =>
    client.delete(`/boards/${boardId}/tasks/${taskId}/comments/${commentId}`),
}

// Task history
export const taskHistoryApi = {
  list: (boardId, taskId) =>
    client.get(`/boards/${boardId}/tasks/${taskId}/history`).then((r) => r.data.map(normalizeHistory)),
}

// Attachments
export const attachmentsApi = {
  list: (boardId, taskId) =>
    client.get(`/boards/${boardId}/tasks/${taskId}/attachments`).then((r) => r.data),
  upload: (boardId, taskId, file) => {
    const fd = new FormData()
    fd.append('file', file)
    return client.post(`/boards/${boardId}/tasks/${taskId}/attachments`, fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data)
  },
  remove: (boardId, taskId, attachmentId) =>
    client.delete(`/boards/${boardId}/tasks/${taskId}/attachments/${attachmentId}`),
}
