import { Navigate } from 'react-router-dom'
import { isAuthenticated } from '../pages/auth/authStorage'

/** Login / signup: skip screen when already logged in. */
export function GuestOnly({ children }) {
  if (isAuthenticated()) {
    return <Navigate to="/admin" replace />
  }
  return children
}
