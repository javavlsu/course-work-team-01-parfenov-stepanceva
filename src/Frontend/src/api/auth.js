import { client } from './client'
import { normalizeUser } from '../utils/normalize'

export const authApi = {
  login: (data) => client.post('/auth/login', data).then((r) => normalizeUser(r.data)),
  register: (data) => client.post('/auth/registration', data).then((r) => normalizeUser(r.data)),
  check: () => client.get('/auth/checkAuth').then((r) => normalizeUser(r.data)),
  refresh: () => client.post('/auth/refresh').then((r) => normalizeUser(r.data)),
  logout: () => client.post('/auth/logout').then((r) => r.data),
}
