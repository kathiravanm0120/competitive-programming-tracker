# Competitive Programming Tracker - Fixed Version

## What was fixed

### Backend
- JWT authentication made stateless and explicit.
- JWT tokens now use HS256 consistently.
- Invalid/expired JWTs return HTTP 401 instead of being silently converted into a confusing 403.
- CORS supports the Vite frontend on `localhost:5173` and `127.0.0.1:5173`.
- Spring Security no longer creates the generated default password.
- Added `/api/auth/me`.
- Added centralized JSON error handling.
- Existing problem CRUD ownership checks are preserved.

### Frontend
- Added Login page.
- Added Register page.
- Added protected routes.
- Login automatically stores the JWT.
- Logout removes authentication data.
- API client automatically attaches the JWT.
- API client redirects to Login when the backend returns 401.
- Problems page uses the real backend data and supports search/filtering.
- Add/Edit/Delete problem flows are connected to PostgreSQL through the Spring Boot API.
- Dashboard now uses tracked problems instead of hard-coded problem statistics.
- Navbar displays the logged-in username.
- Removed fake navigation links that jumped to `#`.

## Run the project

### 1. PostgreSQL

Make sure PostgreSQL is running and the database exists:

`competitive_programming_tracker`

The backend will create/update its tables with JPA.

### 2. Backend

Open Terminal 1:

```powershell
cd backend
mvn spring-boot:run
```

Backend:

`http://localhost:8080`

Health check:

`http://localhost:8080/api/health`

### 3. Frontend

Open Terminal 2:

```powershell
cd frontend
npm install
npm run dev
```

Frontend:

`http://localhost:5173`

### 4. Login

Open:

`http://localhost:5173/login`

Use the existing `testuser` account, or create a new account using Register.

You no longer need to copy JWT tokens manually into DevTools.

## Important

If an old JWT is still in the browser, simply open `/login` and sign in again. The new login flow replaces the old token automatically.

For production, move the PostgreSQL password and JWT secret out of `application.properties` and into environment variables.

## Profile & Platform Accounts

The project now includes a protected Profile page at `/profile`.

Profile features:
- View and update email, bio, and profile image URL
- View account creation date
- View connected platform count
- Connect, update, and remove Codeforces, LeetCode, CodeChef, and AtCoder usernames

Backend endpoints:
- `GET /api/profile`
- `PUT /api/profile`
- `GET /api/profile/platforms`
- `PUT /api/profile/platforms`
- `DELETE /api/profile/platforms/{platform}`

Hibernate/JPA will create the `platform_accounts` table automatically because the project uses `spring.jpa.hibernate.ddl-auto=update`.
