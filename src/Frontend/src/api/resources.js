import { client } from './client'

export const usersApi = {
  me: () => client.get('/users/me').then((r) => r.data),
  update: (data) => client.patch('/users/me', data).then((r) => r.data),
  updatePassword: (data) => client.patch('/users/me/password', data).then((r) => r.data),
  uploadAvatar: (file) => {
    const fd = new FormData()
    fd.append('file', file)
    return client.post('/users/me/avatar', fd, { headers: { 'Content-Type': 'multipart/form-data' } }).then((r) => r.data)
  },
}

export const groupsApi = {
  list: () => client.get('/groups').then((r) => r.data),
  get: (id) => client.get(`/groups/${id}`).then((r) => r.data),
  create: (data) => client.post('/groups', data).then((r) => r.data),
  update: (id, data) => client.patch(`/groups/${id}`, data).then((r) => r.data),
  remove: (id) => client.delete(`/groups/${id}`).then((r) => r.data),
  members: (id) => client.get(`/groups/${id}/members`).then((r) => r.data),
  removeMember: (groupId, userId) => client.delete(`/groups/${groupId}/members/${userId}`).then((r) => r.data),
  updateRole: (groupId, userId, role) => client.patch(`/groups/${groupId}/members/${userId}`, { role }).then((r) => r.data),
}

export const boardsApi = {
  list: (groupId) => client.get(`/groups/${groupId}/boards`).then((r) => r.data),
  get: (id) => client.get(`/boards/${id}`).then((r) => r.data),
  create: (groupId, data) => client.post(`/groups/${groupId}/boards`, data).then((r) => r.data),
  update: (id, data) => client.patch(`/boards/${id}`, data).then((r) => r.data),
  remove: (id) => client.delete(`/boards/${id}`).then((r) => r.data),
}

export const columnsApi = {
  list: (boardId) => client.get(`/boards/${boardId}/columns`).then((r) => r.data),
  create: (boardId, data) => client.post(`/boards/${boardId}/columns`, data).then((r) => r.data),
  update: (id, data) => client.patch(`/columns/${id}`, data).then((r) => r.data),
  remove: (id) => client.delete(`/columns/${id}`).then((r) => r.data),
}

export const tasksApi = {
  list: (boardId) => client.get(`/boards/${boardId}/tasks`).then((r) => r.data),
  get: (id) => client.get(`/tasks/${id}`).then((r) => r.data),
  create: (columnId, data) => client.post(`/columns/${columnId}/tasks`, data).then((r) => r.data),
  update: (id, data) => client.patch(`/tasks/${id}`, data).then((r) => r.data),
  move: (id, data) => client.patch(`/tasks/${id}/move`, data).then((r) => r.data),
  remove: (id) => client.delete(`/tasks/${id}`).then((r) => r.data),
  history: (id) => client.get(`/tasks/${id}/history`).then((r) => r.data),
}

export const commentsApi = {
  list: (taskId) => client.get(`/tasks/${taskId}/comments`).then((r) => r.data),
  create: (taskId, data) => client.post(`/tasks/${taskId}/comments`, data).then((r) => r.data),
  update: (id, data) => client.patch(`/comments/${id}`, data).then((r) => r.data),
  remove: (id) => client.delete(`/comments/${id}`).then((r) => r.data),
}

export const invitationsApi = {
  my: () => client.get('/invitations/my').then((r) => r.data),
  listForGroup: (groupId) => client.get(`/groups/${groupId}/invitations`).then((r) => r.data),
  createEmail: (groupId, data) => client.post(`/groups/${groupId}/invitations/email`, data).then((r) => r.data),
  createLink: (groupId, data) => client.post(`/groups/${groupId}/invitations/link`, data).then((r) => r.data),
  join: (token) => client.post(`/invitations/join/${token}`).then((r) => r.data),
  cancel: (id) => client.delete(`/invitations/${id}`).then((r) => r.data),
}
