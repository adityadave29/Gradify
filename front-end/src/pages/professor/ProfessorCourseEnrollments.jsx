import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { api } from '../../api/client'

function ProfessorCourseEnrollments() {
  const { courseId } = useParams()
  const navigate = useNavigate()
  const [students, setStudents] = useState([])
  const [course, setCourse] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function fetchData() {
      setLoading(true)
      try {
        // Fetch course details and students in parallel
        const [courseRes, studentsRes] = await Promise.all([
          api.get(`/api/professor/courses/${courseId}`),
          api.get(`/api/professor/courses/${courseId}/students`)
        ])
        setCourse(courseRes.data)
        setStudents(studentsRes.data)
      } catch (err) {
        console.error('Failed to fetch enrollment data:', err)
        setError('Could not load enrollment data. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    if (courseId) {
      fetchData()
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
              <div className="mb-1 inline-flex items-center rounded-full bg-emerald-500/10 px-2.5 py-0.5 text-[10px] font-bold uppercase tracking-wider text-emerald-400 ring-1 ring-inset ring-emerald-500/20">
                Enrollment List
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
            <div className="mb-4 h-8 w-8 animate-spin rounded-full border-2 border-zinc-800 border-t-emerald-500"></div>
            <p className="text-sm">Fetching enrollment data...</p>
          </div>
        ) : error ? (
          <div className="rounded-2xl border border-red-500/20 bg-red-500/5 p-12 text-center text-red-400">
            <svg className="mx-auto mb-4 h-12 w-12 text-red-500/50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p className="font-medium">{error}</p>
            <button 
              onClick={() => window.location.reload()}
              className="mt-4 text-xs font-semibold text-zinc-400 underline underline-offset-4 hover:text-zinc-100"
            >
              Try Again
            </button>
          </div>
        ) : (
          <div className="overflow-hidden rounded-2xl border border-zinc-800 bg-zinc-900/50 backdrop-blur-sm animate-in fade-in slide-in-from-bottom-4 duration-500">
            <table className="w-full text-left text-sm text-zinc-300">
              <thead className="bg-zinc-800/50 text-xs font-semibold uppercase tracking-wider text-zinc-500">
                <tr>
                  <th className="px-6 py-4">Student Name</th>
                  <th className="px-6 py-4">Email Address</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-800">
                {students.length > 0 ? (
                  students.map((student, idx) => (
                    <tr key={idx} className="transition hover:bg-zinc-800/30">
                      <td className="px-6 py-4 font-medium text-zinc-200">
                        <div className="flex items-center gap-3">
                          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-zinc-800 text-[10px] font-bold text-zinc-400">
                            {student.name?.split(' ').map(n => n[0]).join('').toUpperCase()}
                          </div>
                          {student.name}
                        </div>
                      </td>
                      <td className="px-6 py-4 text-zinc-400">{student.email}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="2" className="px-6 py-20 text-center">
                      <div className="flex flex-col items-center">
                        <svg className="mb-4 h-12 w-12 text-zinc-800" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        <p className="text-zinc-500">No students enrolled in this course yet.</p>
                      </div>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default ProfessorCourseEnrollments
