import { client } from './client'

export const authApi = {
  login: (data) => client.post('/auth/login', data).then((r) => r.data),
  register: (data) => client.post('/auth/register', data).then((r) => r.data),
  logout: () => client.post('/auth/logout').then((r) => r.data),
  refresh: () => client.post('/auth/refresh').then((r) => r.data),
  me: () => client.get('/users/me').then((r) => r.data),
}
