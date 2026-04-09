import { Link, useNavigate } from 'react-router-dom'
import { clearSession, getStoredUser } from '../auth/authStorage'

function AdminHomepage() {
  const navigate = useNavigate()
  const user = getStoredUser()

  const handleLogout = () => {
    clearSession()
    navigate('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-8 bg-zinc-950 px-4 text-zinc-100">
      <div className="text-center">
        <h1 className="text-3xl font-semibold tracking-tight">Admin service</h1>
        <p className="mt-2 text-sm text-zinc-400">Gateway: /api/admin/*</p>
      </div>
      {user?.email && (
        <p className="text-sm text-zinc-400">Signed in as {user.email}</p>
      )}
      <nav className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <Link
          to="/admin-service/create-user"
          className="rounded-lg border border-zinc-600 px-4 py-2 text-center text-sm text-zinc-300 transition hover:border-zinc-500 hover:bg-zinc-900"
        >
          Create user
        </Link>
        <Link
          to="/admin-service/create-course"
          className="rounded-lg border border-zinc-600 px-4 py-2 text-center text-sm text-zinc-300 transition hover:border-zinc-500 hover:bg-zinc-900"
        >
          Create course
        </Link>
        <button
          type="button"
          onClick={handleLogout}
          className="rounded-lg border border-zinc-600 px-4 py-2 text-sm text-zinc-300 transition hover:border-zinc-500 hover:bg-zinc-900"
        >
          Log out
        </button>
      </nav>
    </div>
  )
}

export default AdminHomepage
