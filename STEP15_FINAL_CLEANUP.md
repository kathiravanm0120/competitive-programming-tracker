# Step 15 — Final Cleanup & Bug-Fix Pass

## Included
- Removed unused Vite starter assets/CSS.
- Centralized auth cleanup now removes token, username, and role together.
- Added `frontend/.env.example` for API configuration.
- Preserved existing feature modules from Steps 1–14.

## Local verification
Backend:
```powershell
cd backend
.\mvnw.cmd clean test
```

Frontend:
```powershell
cd frontend
npm install
npm run build
```

## Release checks
- Do not commit `.env` files or real credentials.
- Set `DB_PASSWORD`, `JWT_SECRET`, and other production variables through the environment.
- Verify `/api/health`, login, role-based admin access, and report downloads after deployment.
