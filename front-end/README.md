# Gradify Front-End

React + Vite frontend for Gradify.

## Current Features

- **Login**: `src/pages/auth/login.jsx`
  - Calls `POST http://localhost:8081/api/auth/login` with JSON `{ email, password }`
  - Logs the full response to console (including `access_token`, `refresh_token`, etc. when present)
- **JWT/session persistence (frontend)**: `src/pages/auth/authStorage.js`
  - Stores tokens + expiry in `localStorage`
  - `isAuthenticated()` is used for routing decisions
- **Route guards**
  - `/login` is **guest-only** (if already authenticated → redirects to `/admin`)
  - `/admin` is **protected** (if not authenticated → redirects to `/login`)
- **Logout**
  - Available on `/admin` and clears `localStorage` session, then redirects to `/login`

## Routes

- `/` → redirects to `/admin` when authenticated, otherwise `/login`
- `/login` → login screen
- `/admin` → protected home page

## Tech Stack

- React
- Vite
- React Router DOM
- Axios
- Tailwind CSS

## Notes

- **Signup flow/pages are intentionally removed for now** and will be added later.
- Access tokens expire (e.g. `expires_in: 3600`). Refresh-token rotation is not implemented yet; once expired, the user will be prompted to log in again.
