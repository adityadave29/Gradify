import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/auth/login'
import AdminHomePage from './pages/admin/AdminHomePage'
import AdminHomepage from './pages/admin-service/admin-homepage'
import CreateUser from './pages/admin-service/create-user'
import CreateUserDetails from './pages/admin-service/create-user-details'
import CreateCourse from './pages/admin-service/create-course'
import { GuestOnly } from './components/GuestOnly'
import { RequireAuth } from './components/RequireAuth'
import { isAuthenticated } from './pages/auth/authStorage'

function RootRedirect() {
  return isAuthenticated() ? (
    <Navigate to="/admin-service" replace />
  ) : (
    <Navigate to="/login" replace />
  )
}

function NotFoundRedirect() {
  return isAuthenticated() ? (
    <Navigate to="/admin-service" replace />
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
        <Route
          path="/admin-service"
          element={
            <RequireAuth>
              <AdminHomepage />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/create-user"
          element={
            <RequireAuth>
              <CreateUser />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/create-user/details"
          element={
            <RequireAuth>
              <CreateUserDetails />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/create-course"
          element={
            <RequireAuth>
              <CreateCourse />
            </RequireAuth>
          }
        />
        <Route path="*" element={<NotFoundRedirect />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
