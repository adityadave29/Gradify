# Gradify Front-End Progress

This front-end is built with React + Vite and currently includes the initial authentication flow and routing setup.

## Progress Completed

- Project cleanup done:
  - Removed unnecessary starter boilerplate from `src/App.jsx` and `src/main.jsx`
  - Removed `src/App.css`
- Folder structure created under `src/`:
  - `pages/`
  - `components/`
- Routing added using `react-router-dom` in `src/App.jsx`:
  - `/login` -> Login page
  - `/signup` -> Signup page
  - `/admin` -> Admin home page
  - `/` and unknown routes redirect to `/login`
- Auth pages implemented:
  - `src/pages/auth/login.jsx`
  - `src/pages/auth/signup.jsx`
- API integration with Axios:
  - Login POST -> `http://localhost:8081/api/auth/login`
  - Signup POST -> `http://localhost:8081/api/auth/signup`
- Navigation flow:
  - Login success redirects to `/admin`
  - Login page includes button to navigate to Signup (`No Account? Then signup page`)
  - Signup success redirects back to `/login`
- Admin page added:
  - `src/pages/admin/AdminHomePage.jsx` with centered "Homepage" text
- UI styling:
  - Tailwind CSS-based black/grey theme applied to Login and Signup pages

## Current Tech Stack

- React
- Vite
- React Router DOM
- Axios
- Tailwind CSS

## Next Suggested Steps

- Add client-side form validation (email format, password rules)
- Store auth token (if backend returns one) and protect `/admin` route
- Add logout flow
- Add reusable input/button components in `src/components/`
