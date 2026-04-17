import { create } from 'zustand'

// Session lives server-side (cookies). Local store holds user profile + a "checked" flag.
export const useAuthStore = create((set) => ({
  user: null,
  isAuthenticated: false,
  isChecked: false,
  setUser: (user) => set({ user, isAuthenticated: !!user, isChecked: true }),
  markChecked: () => set({ isChecked: true }),
  logout: () => set({ user: null, isAuthenticated: false, isChecked: true }),
}))
