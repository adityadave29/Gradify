import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { clearSession, getStoredUser } from '../auth/authStorage'
import { api } from '../../api/client'

function ProfessorHomepage() {
  const navigate = useNavigate()
  const user = getStoredUser()
  const [courses, setCourses] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleLogout = () => {
    clearSession()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-zinc-950 px-4 py-10 text-zinc-100">
      <div className="mx-auto flex w-full max-w-5xl flex-col items-center gap-8">
        <div className="text-center">
          <div className="mb-4 inline-flex items-center justify-center rounded-full bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-400 ring-1 ring-inset ring-emerald-500/20">
            Professor Portal
          </div>
          <h1 className="text-3xl font-semibold tracking-tight">Welcome, {user?.email?.split('@')[0] || 'Professor'}</h1>
          <p className="mt-2 text-sm text-zinc-400">Manage your courses, students, and grading.</p>
        </div>

        <nav className="flex items-center gap-4">
          <button
            type="button"
            onClick={handleLogout}
            className="rounded-lg border border-zinc-700 bg-zinc-900 px-5 py-2 text-sm font-medium text-zinc-300 transition hover:border-zinc-500 hover:bg-zinc-800"
          >
            Log out
          </button>
        </nav>

        <section className="w-full">
          <div className="mb-6 flex items-center justify-between border-b border-zinc-800 pb-4">
            <h2 className="text-xl font-semibold tracking-tight text-zinc-100">Teaching Courses</h2>
          </div>

          <div className="rounded-2xl border border-dashed border-zinc-800 bg-zinc-900/30 p-12 text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-zinc-800/50">
              <svg className="h-6 w-6 text-zinc-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <h3 className="text-sm font-medium text-zinc-300">No courses assigned</h3>
            <p className="mt-1 text-xs text-zinc-500">You are currently not teaching any courses.</p>
          </div>
        </section>
      </div>
    </div>
  )
}

export default ProfessorHomepage
