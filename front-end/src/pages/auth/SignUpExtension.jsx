import { useEffect, useState } from 'react'
import axios from 'axios'
import { useLocation, useNavigate } from 'react-router-dom'

const API_BASE = 'http://localhost:8081'
const ROLES = ['STUDENT', 'PROFESSOR', 'ADMIN']

function SignUpExtension() {
  const navigate = useNavigate()
  const location = useLocation()
  const emailFromSignup = location.state?.email ?? ''

  const [name, setName] = useState('')
  const [role, setRole] = useState('STUDENT')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (!emailFromSignup) {
      navigate('/signup', { replace: true })
    }
  }, [emailFromSignup, navigate])

  const handleSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await axios.post(
        `${API_BASE}/api/auth/profile`,
        {
          email: emailFromSignup,
          name,
          role,
        },
        {
          headers: { 'Content-Type': 'application/json' },
        }
      )

      console.log('Profile response:', response.data)
      navigate('/login')
    } catch (error) {
      setMessage(error.response?.data?.message || 'Unable to reach server')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  if (!emailFromSignup) {
    return null
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-950 px-4 text-zinc-100">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md rounded-2xl border border-zinc-800 bg-zinc-900/80 p-8 shadow-2xl shadow-black/40"
      >
        <h1 className="mb-2 text-2xl font-semibold tracking-tight text-zinc-100">
          Complete your profile
        </h1>
        <p className="mb-2 text-sm text-zinc-400">
          Signed up as <span className="text-zinc-300">{emailFromSignup}</span>
        </p>
        <p className="mb-6 text-sm text-zinc-400">Add your name and role to finish registration.</p>

        <div className="mb-4">
          <label htmlFor="name" className="mb-2 block text-sm text-zinc-300">
            Name
          </label>
          <input
            id="name"
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3 py-2 text-zinc-100 outline-none transition focus:border-zinc-500 focus:ring-2 focus:ring-zinc-500/30"
            placeholder="Your full name"
          />
        </div>

        <div className="mb-6">
          <label htmlFor="role" className="mb-2 block text-sm text-zinc-300">
            Role
          </label>
          <select
            id="role"
            value={role}
            onChange={(event) => setRole(event.target.value)}
            required
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3 py-2 text-zinc-100 outline-none transition focus:border-zinc-500 focus:ring-2 focus:ring-zinc-500/30"
          >
            {ROLES.map((r) => (
              <option key={r} value={r}>
                {r}
              </option>
            ))}
          </select>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg bg-zinc-200 px-4 py-2.5 font-medium text-zinc-900 transition hover:bg-zinc-300 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {loading ? 'Submitting...' : 'Complete signup'}
        </button>

        {message && (
          <p className="mt-4 text-sm text-zinc-300" role="status">
            {message}
          </p>
        )}
      </form>
    </div>
  )
}

export default SignUpExtension
