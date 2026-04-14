import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/auth/login'
import AdminHomePage from './pages/admin/AdminHomePage'
import AdminHomepage from './pages/admin-service/admin-homepage'
import CreateUser from './pages/admin-service/create-user'
import CreateUserDetails from './pages/admin-service/create-user-details'
import CreateCourse from './pages/admin-service/create-course'
import AddCourseUsers from './pages/admin-service/add-course-users'
import StudentHomepage from './pages/student/StudentHomepage'
import ProfessorHomepage from './pages/professor/ProfessorHomepage'
import ProfessorCourseEnrollments from './pages/professor/ProfessorCourseEnrollments'
import { GuestOnly } from './components/GuestOnly'
import { RequireAuth } from './components/RequireAuth'
import { getUserRole, isAuthenticated } from './pages/auth/authStorage'

function RootRedirect() {
  if (!isAuthenticated()) return <Navigate to="/login" replace />
  const role = (getUserRole() || '').toUpperCase()
  if (role === 'ADMIN') return <Navigate to="/admin-service" replace />
  if (role === 'PROFESSOR') return <Navigate to="/professor" replace />
  if (role === 'STUDENT') return <Navigate to="/student" replace />
  return <Navigate to="/login" replace />
}

function NotFoundRedirect() {
  return <RootRedirect />
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
            <RequireAuth allowedRole="ADMIN">
              <AdminHomePage />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service"
          element={
            <RequireAuth allowedRole="ADMIN">
              <AdminHomepage />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/create-user"
          element={
            <RequireAuth allowedRole="ADMIN">
              <CreateUser />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/create-user/details"
          element={
            <RequireAuth allowedRole="ADMIN">
              <CreateUserDetails />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/create-course"
          element={
            <RequireAuth allowedRole="ADMIN">
              <CreateCourse />
            </RequireAuth>
          }
        />
        <Route
          path="/admin-service/courses/:courseId/users"
          element={
            <RequireAuth allowedRole="ADMIN">
              <AddCourseUsers />
            </RequireAuth>
          }
        />
        <Route
          path="/student"
          element={
            <RequireAuth allowedRole="STUDENT">
              <StudentHomepage />
            </RequireAuth>
          }
        />
        <Route
          path="/professor"
          element={
            <RequireAuth allowedRole="PROFESSOR">
              <ProfessorHomepage />
            </RequireAuth>
          }
        />
        <Route
          path="/professor/courses/:courseId/students"
          element={
            <RequireAuth allowedRole="PROFESSOR">
              <ProfessorCourseEnrollments />
            </RequireAuth>
          }
        />
        <Route path="*" element={<NotFoundRedirect />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
