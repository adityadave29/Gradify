const KEYS = {
  access: 'gradify_access_token',
  refresh: 'gradify_refresh_token',
  expiresAt: 'gradify_token_expires_at',
  user: 'gradify_user',
}

/** ~1 min before JWT expiry we treat session as ended (clock skew / margin). */
const EXPIRY_MARGIN_MS = 60_000

export function saveSession(body) {
  if (!body || typeof body !== 'object') return

  const { access_token, refresh_token, expires_in, user } = body

  if (access_token) {
    localStorage.setItem(KEYS.access, access_token)
  }
  if (refresh_token) {
    localStorage.setItem(KEYS.refresh, refresh_token)
  }

  const seconds = typeof expires_in === 'number' ? expires_in : 3600
  localStorage.setItem(KEYS.expiresAt, String(Date.now() + seconds * 1000))

  if (user !== undefined && user !== null) {
    localStorage.setItem(KEYS.user, JSON.stringify(user))
  }
}

export function clearSession() {
  Object.values(KEYS).forEach((key) => localStorage.removeItem(key))
}

export function getAccessToken() {
  return localStorage.getItem(KEYS.access)
}

export function getRefreshToken() {
  return localStorage.getItem(KEYS.refresh)
}

export function getStoredUser() {
  const raw = localStorage.getItem(KEYS.user)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function isAuthenticated() {
  const token = getAccessToken()
  const expRaw = localStorage.getItem(KEYS.expiresAt)
  if (!token || !expRaw) return false

  const expiresAt = Number(expRaw)
  if (!Number.isFinite(expiresAt)) return false

  return Date.now() < expiresAt - EXPIRY_MARGIN_MS
}
