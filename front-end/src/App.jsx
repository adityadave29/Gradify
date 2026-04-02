import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/auth/login'
import AdminHomePage from './pages/admin/AdminHomePage'
import { GuestOnly } from './components/GuestOnly'
import { RequireAuth } from './components/RequireAuth'
import { isAuthenticated } from './pages/auth/authStorage'

function RootRedirect() {
  return isAuthenticated() ? (
    <Navigate to="/admin" replace />
  ) : (
    <Navigate to="/login" replace />
  )
}

function NotFoundRedirect() {
  return isAuthenticated() ? (
    <Navigate to="/admin" replace />
  ) : (
    <Navigate to="/login" replace />
  )
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RootRedirect />} />
        <Route
          path="/login"
          element={
            <GuestOnly>
              <Login />
            </GuestOnly>
          }
        />
        <Route
          path="/admin"
          element={
            <RequireAuth>
              <AdminHomePage />
            </RequireAuth>
          }
        />
        <Route path="*" element={<NotFoundRedirect />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
