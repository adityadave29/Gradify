import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/auth/login'
import Signup from './pages/auth/signup'
import AdminHomePage from './pages/admin/AdminHomePage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/admin" element={<AdminHomePage />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
