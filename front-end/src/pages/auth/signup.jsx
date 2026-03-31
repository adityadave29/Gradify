import { useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

function Signup() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await axios.post('http://localhost:8081/api/auth/signup', {
        email,
        password,
      })

      setMessage('Signup successful')
      console.log('Signup response:', response.data)
      navigate('/login')
    } catch (error) {
      setMessage(error.response?.data?.message || 'Unable to reach server')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-950 px-4 text-zinc-100">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md rounded-2xl border border-zinc-800 bg-zinc-900/80 p-8 shadow-2xl shadow-black/40"
      >
        <h1 className="mb-2 text-2xl font-semibold tracking-tight text-zinc-100">
          Create Account
        </h1>
        <p className="mb-6 text-sm text-zinc-400">Sign up to continue</p>

        <div className="mb-4">
          <label htmlFor="email" className="mb-2 block text-sm text-zinc-300">
            Email
          </label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3 py-2 text-zinc-100 outline-none transition focus:border-zinc-500 focus:ring-2 focus:ring-zinc-500/30"
            placeholder="you@example.com"
          />
        </div>

        <div className="mb-6">
          <label htmlFor="password" className="mb-2 block text-sm text-zinc-300">
            Password
          </label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3 py-2 text-zinc-100 outline-none transition focus:border-zinc-500 focus:ring-2 focus:ring-zinc-500/30"
            placeholder="Create a password"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg bg-zinc-200 px-4 py-2.5 font-medium text-zinc-900 transition hover:bg-zinc-300 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {loading ? 'Submitting...' : 'Sign Up'}
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

export default Signup
