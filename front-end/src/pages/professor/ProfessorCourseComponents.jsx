import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { api } from '../../api/client'

function ProfessorCourseComponents() {
  const { courseId } = useParams()
  const navigate = useNavigate()
  const [course, setCourse] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function fetchCourse() {
      setLoading(true)
      try {
        const response = await api.get(`/api/professor/courses/${courseId}`)
        setCourse(response.data)
      } catch (err) {
        console.error('Failed to fetch course details:', err)
        setError('Could not load course details. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    if (courseId) {
      fetchCourse()
    }
  }, [courseId])

  return (
    <div className="min-h-screen bg-zinc-950 px-4 py-10 text-zinc-100">
      <div className="mx-auto flex w-full max-w-5xl flex-col gap-8">
        <div className="flex items-center justify-between border-b border-zinc-800 pb-6">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate('/professor')}
              className="group flex h-10 w-10 items-center justify-center rounded-xl bg-zinc-900 border border-zinc-800 text-zinc-400 transition hover:border-zinc-600 hover:text-zinc-100"
            >
              <svg className="h-5 w-5 transition group-hover:-translate-x-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
              </svg>
            </button>
            <div>
              <div className="mb-1 inline-flex items-center rounded-full bg-blue-500/10 px-2.5 py-0.5 text-[10px] font-bold uppercase tracking-wider text-blue-400 ring-1 ring-inset ring-blue-500/20">
                Evaluation Admin
              </div>
              <h1 className="text-2xl font-semibold tracking-tight text-zinc-100">
                {loading ? 'Loading Course...' : course?.course_name || 'Course Not Found'}
              </h1>
              <p className="text-sm text-zinc-500">{course?.course_code}</p>
            </div>
          </div>
        </div>

        {loading ? (
          <div className="flex flex-col items-center justify-center py-20 text-zinc-500">
            <div className="mb-4 h-8 w-8 animate-spin rounded-full border-2 border-zinc-800 border-t-blue-500"></div>
            <p className="text-sm">Fetching course data...</p>
          </div>
        ) : error ? (
          <div className="rounded-2xl border border-red-500/20 bg-red-500/5 p-12 text-center text-red-400">
            <svg className="mx-auto mb-4 h-12 w-12 text-red-500/50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p className="font-medium">{error}</p>
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-20 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-zinc-900 border border-zinc-800">
              <svg className="h-8 w-8 text-zinc-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-zinc-200">Evaluation Components Dashboard</h3>
            <p className="mt-2 text-zinc-500 text-center max-w-sm">
              This is where you will manage exams, quizzes, and project weightages for 
              <span className="text-zinc-300 font-medium"> {course.course_name}</span>.
            </p>
            <div className="mt-8 rounded-full bg-zinc-800 px-4 py-1.5 text-xs font-medium text-zinc-400">
              Coming Soon
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default ProfessorCourseComponents
