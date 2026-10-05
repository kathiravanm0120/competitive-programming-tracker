# Competitive Programming Tracker — Deployment Guide

This guide details how to deploy the **Competitive Programming Tracker** full-stack application using **Free Cloud Platforms**:
- **Backend & Database**: Render (Spring Boot 4.1 + PostgreSQL 16)
- **Frontend**: Vercel or Netlify (React 19 + Vite 8 SPA)

---

## 🛠️ Deployment Summary Architecture

```
[ User Browser ]
       │
       ├──► Frontend (Vercel / Netlify) ──► React Single Page App
       │
       └──► Backend (Render Web Service) ──► Spring Boot API (:8080)
                   │
                   └──► Database (Render PostgreSQL)
```

---

## 🚀 Step 1: Deploy Backend & Database on Render

### Option A: Automated 1-Click Deployment (Recommended)

1. Push your repository to GitHub / GitLab.
2. Sign in to [Render Dashboard](https://dashboard.render.com/).
3. Click **New +** ──► **Blueprint**.
4. Connect your repository containing `render.yaml`.
5. Render will automatically detect `render.yaml` and provision:
   - **`cpt-postgres`**: Free PostgreSQL database.
   - **`cpt-backend`**: Dockerized Spring Boot Web Service.
6. Once deployed, note down your backend live URL:
   `https://cpt-backend-xxxx.onrender.com`

---

### Option B: Manual Setup on Render

#### 1. Create PostgreSQL Database:
1. On Render, click **New +** ──► **PostgreSQL**.
2. Name: `cpt-postgres`
3. Database: `competitive_programming_tracker`
4. User: `cpt_user`
5. Select **Free** plan, then click **Create Database**.
6. Save the **Internal Database URL** provided by Render.

#### 2. Create Spring Boot Web Service:
1. Click **New +** ──► **Web Service**.
2. Select your repository.
3. Settings:
   - **Runtime**: `Docker` (Dockerfile location: `backend/Dockerfile`, Context: `backend`)
   - **Plan**: Free
4. Add **Environment Variables**:

| Variable Name | Value / Format | Notes |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://<host>:5432/competitive_programming_tracker` | Use host from Render Internal DB URL |
| `DB_USERNAME` | `cpt_user` | From Render DB |
| `DB_PASSWORD` | `<your-db-password>` | From Render DB |
| `JWT_SECRET` | `<32+ character random string>` | Secure random secret key |
| `CORS_ALLOWED_ORIGINS` | `https://<your-frontend>.vercel.app` | Updated after deploying frontend |

---

## 🌐 Step 2: Deploy Frontend on Vercel / Netlify

### Deploying on Vercel

1. Log in to [Vercel Dashboard](https://vercel.com/dashboard).
2. Click **Add New...** ──► **Project**.
3. Import your repository.
4. Framework Preset: **Vite**
5. Root Directory: `frontend`
6. Under **Environment Variables**, add:
   - **Name**: `VITE_API_URL`
   - **Value**: `https://cpt-backend-xxxx.onrender.com/api` *(Your Render backend URL + `/api`)*
7. Click **Deploy**.

> Note: `frontend/vercel.json` is included to ensure all client side routes resolve cleanly to `index.html`.

---

### Deploying on Netlify

1. Log in to [Netlify Dashboard](https://app.netlify.com/).
2. Click **Add new site** ──► **Import an existing project**.
3. Connect your repository.
4. Settings:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/dist`
5. Under **Environment Variables**, add:
   - `VITE_API_URL`: `https://cpt-backend-xxxx.onrender.com/api`
6. Click **Deploy site**.

> Note: `frontend/netlify.toml` and `frontend/public/_redirects` are included for seamless SPA routing.

---

## 🔒 Step 3: Final Configuration & Verification

1. **Update Backend CORS**:
   In your Render backend service settings, ensure `CORS_ALLOWED_ORIGINS` includes your live frontend URL (e.g. `https://your-app.vercel.app`).

2. **Verify Backend Health**:
   Visit: `https://cpt-backend-xxxx.onrender.com/api/health`  
   Expected Output: `"Competitive Programming Tracker API is running!"`

3. **Verify App Functionality**:
   - Open your frontend domain.
   - Go to `/register` and register your account.
   - Test platform fetching (Codeforces, LeetCode, CodeChef, AtCoder) and goal planning.

---

## 🐳 Alternative: Self-Hosted Docker Deployment (VPS / Local)

If deploying to your own Virtual Private Server (DigitalOcean, AWS EC2, Linode):

1. Install Docker & Docker Compose on your server.
2. Clone the repository.
3. Run:
   ```bash
   docker-compose up -d --build
   ```
4. App will be running at:
   - Backend: `http://<server-ip>:8080/api`
   - Database: `localhost:5432`

---

## 📋 Deployment File Directory

- [`render.yaml`](file:///d:/projects/competitive-programming-tracker/render.yaml) — Render Blueprint for 1-Click deployment
- [`backend/Dockerfile`](file:///d:/projects/competitive-programming-tracker/backend/Dockerfile) — Multi-stage Spring Boot Java 21 Dockerfile
- [`frontend/vercel.json`](file:///d:/projects/competitive-programming-tracker/frontend/vercel.json) — Vercel SPA route rewrite rules
- [`frontend/netlify.toml`](file:///d:/projects/competitive-programming-tracker/frontend/netlify.toml) — Netlify build & redirect rules
- [`docker-compose.yml`](file:///d:/projects/competitive-programming-tracker/docker-compose.yml) — Production Docker Compose stack
