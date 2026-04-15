import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { clearSession, getStoredUser } from '../auth/authStorage'
import { api } from '../../api/client'

function StudentHomepage() {
  const navigate = useNavigate()
  const user = getStoredUser()
  const [courses, setCourses] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user?.id) return

    async function fetchCourses() {
      setLoading(true)
      try {
        const response = await api.get(`/api/student/courses?email=${encodeURIComponent(user.email)}`)
        setCourses(response.data)
      } catch (err) {
        console.error('Failed to fetch enrolled courses:', err)
        setError('Could not load your courses. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    fetchCourses()
  }, [user?.id])

  const handleLogout = () => {
    clearSession()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-zinc-950 px-4 py-10 text-zinc-100">
      <div className="mx-auto flex w-full max-w-5xl flex-col items-center gap-8">

        {/* Header */}
        <div className="text-center">
          <div className="mb-4 inline-flex items-center justify-center rounded-full bg-blue-500/10 px-3 py-1 text-xs font-medium text-blue-400 ring-1 ring-inset ring-blue-500/20">
            Student Portal
          </div>
          <h1 className="text-3xl font-semibold tracking-tight">
            Welcome, {user?.name || user?.email?.split('@')[0] || 'Student'}
          </h1>
          <p className="mt-2 text-sm text-zinc-400">View your enrolled courses below.</p>
        </div>

        {/* Logout button */}
        <nav className="flex items-center gap-4">
          <button
            id="student-logout-btn"
            type="button"
            onClick={handleLogout}
            className="rounded-lg border border-zinc-700 bg-zinc-900 px-5 py-2 text-sm font-medium text-zinc-300 transition hover:border-zinc-500 hover:bg-zinc-800"
          >
            Log out
          </button>
        </nav>

        {/* My Courses section */}
        <section className="w-full">
          <div className="mb-6 flex items-center justify-between border-b border-zinc-800 pb-4">
            <h2 className="text-xl font-semibold tracking-tight text-zinc-100">My Courses</h2>
            {!loading && courses.length > 0 && (
              <span className="rounded-full bg-blue-500/10 px-3 py-1 text-xs font-medium text-blue-400 ring-1 ring-inset ring-blue-500/20">
                {courses.length} {courses.length === 1 ? 'course' : 'courses'}
              </span>
            )}
          </div>

          {loading ? (
            <div className="py-12 text-center text-zinc-500">Loading courses...</div>
          ) : error ? (
            <div className="rounded-xl border border-red-500/20 bg-red-500/5 p-6 text-center text-red-400">
              {error}
            </div>
          ) : courses.length > 0 ? (
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {courses.map((course) => (
                <div
                  key={course.id}
                  id={`course-card-${course.id}`}
                  className="group flex flex-col justify-between rounded-2xl border border-zinc-800 bg-zinc-900/50 p-6 transition hover:border-zinc-700 hover:bg-zinc-800/80"
                >
                  <div>
                    <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-zinc-800 transition group-hover:bg-zinc-700">
                      <svg className="h-5 w-5 text-zinc-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                    </div>
                    <h3 className="font-semibold text-zinc-100">{course.course_name}</h3>
                    <p className="mt-1 text-sm text-zinc-500">{course.course_code}</p>
                  </div>

                  <div className="mt-6 flex items-center gap-2">
                    <div className="h-2 w-2 rounded-full bg-blue-400" />
                    <span className="text-xs text-zinc-500">Enrolled</span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="rounded-2xl border border-dashed border-zinc-800 bg-zinc-900/30 p-12 text-center">
              <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-zinc-800/50">
                <svg className="h-6 w-6 text-zinc-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
              <h3 className="text-sm font-medium text-zinc-300">No active enrollments</h3>
              <p className="mt-1 text-xs text-zinc-500">Contact your administrator to enroll in courses.</p>
            </div>
          )}
        </section>

      </div>
    </div>
  )
}

export default StudentHomepage
