import { Navigate } from 'react-router-dom'
import { isAuthenticated } from '../pages/auth/authStorage'

export function GuestOnly({ children }) {
  if (isAuthenticated()) return <Navigate to="/admin" replace />
  return children
}
