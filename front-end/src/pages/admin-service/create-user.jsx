import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../api/client'

function CreateUser() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      await api.post('/api/admin/users', { email, password })
      setMessage('User created')
      setEmail('')
      setPassword('')
    } catch (error) {
      setMessage(
        error.response?.data?.error ||
          error.response?.data?.message ||
          'Unable to reach server'
      )
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-zinc-950 px-4 py-10 text-zinc-100">
      <div className="mb-6 w-full max-w-md">
        <Link
          to="/admin-service"
          className="text-sm text-zinc-400 transition hover:text-zinc-200"
        >
          ← Admin home
        </Link>
      </div>
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md rounded-2xl border border-zinc-800 bg-zinc-900/80 p-8 shadow-2xl shadow-black/40"
      >
        <h1 className="mb-6 text-center text-2xl font-semibold tracking-tight">Create user</h1>

        <div className="mb-4">
          <label htmlFor="admin-create-email" className="mb-2 block text-sm text-zinc-300">
            Email
          </label>
          <input
            id="admin-create-email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3 py-2 text-zinc-100 outline-none transition focus:border-zinc-500 focus:ring-2 focus:ring-zinc-500/30"
            placeholder="user@example.com"
          />
        </div>

        <div className="mb-6">
          <label htmlFor="admin-create-password" className="mb-2 block text-sm text-zinc-300">
            Password
          </label>
          <input
            id="admin-create-password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3 py-2 text-zinc-100 outline-none transition focus:border-zinc-500 focus:ring-2 focus:ring-zinc-500/30"
            placeholder="Temporary password"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg bg-zinc-200 px-4 py-2.5 font-medium text-zinc-900 transition hover:bg-zinc-300 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {loading ? 'Creating…' : 'Create user'}
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

export default CreateUser
