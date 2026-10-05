# Competitive Programming Tracker — Local Run Guide

## 1. PostgreSQL

Create/start PostgreSQL and make sure this database exists:

`competitive_programming_tracker`

The backend reads database settings from environment variables. See `backend/.env.example`.

## 2. Backend

Open a terminal:

```powershell
cd D:\projects\competitive-programming-tracker\backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Health check:

`http://localhost:8080/api/health`

Expected response:

`Competitive Programming Tracker API is running!`

Keep the backend terminal running.

## 3. Frontend

Open a second terminal:

```powershell
cd D:\projects\competitive-programming-tracker\frontend
npm install
npm run dev
```

Open:

`http://localhost:5173`

The frontend expects the backend at:

`http://localhost:8080/api`

To override it, create `frontend/.env` with:

```text
VITE_API_URL=http://localhost:8080/api
```

## 4. First login

Open `/register`, create an account, then log in.

Admin access is controlled by the backend role. See `ADMIN_SETUP.md`.

## 5. If the browser still shows an old Vite error

Stop the frontend with `Ctrl + C`, then start it again:

```powershell
npm run dev
```

Then perform a hard refresh with `Ctrl + Shift + R`.

Do not copy `node_modules` from another operating system. Run `npm install` in the frontend directory so Vite installs native dependencies for the current machine.
