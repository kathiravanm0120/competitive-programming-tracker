# Step 14 — Testing & Security Hardening

## Automated backend tests

Run from `backend`:

```powershell
.\mvnw.cmd clean test
```

The suite includes:

- Application context smoke test
- JWT generation/extraction and tamper rejection
- Registration/password hashing checks
- Login/disabled-account checks
- Admin self-protection checks
- Public health endpoint check
- Protected endpoint requires JWT
- CORS preflight check
- Weak registration password rejection
- USER cannot access `/api/admin/**`
- ADMIN can access `/api/admin/**`
- Disabled users cannot access protected endpoints

## Manual security checklist

| Area | Test | Expected |
|---|---|---|
| Auth | Invalid password | 401 |
| Auth | Expired JWT | 401 |
| Auth | Missing JWT | 401 |
| Auth | Malformed Authorization | 401 |
| Authorization | USER → `/api/admin/**` | 403 |
| Authorization | ADMIN → `/api/admin/**` | 200 |
| Ownership | User A edits User B problem | 403 |
| Ownership | User A deletes User B problem | 403 |
| Admin | Disable own admin account | 403 |
| Admin | Remove own admin role | 403 |
| CORS | Allowed frontend origin | 200 preflight |
| CORS | Unknown origin | Not allowed |
| Validation | Weak registration password | 400 |
| Validation | Blank required fields | 400 |

## Production configuration

Set these environment variables in production:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION_MS
CORS_ALLOWED_ORIGINS
ADMIN_BOOTSTRAP_USERNAME
SERVER_PORT
```

Do not commit real secrets to GitHub. `application.properties` now reads these values from environment variables with development-only fallbacks.

For production, leave `ADMIN_BOOTSTRAP_USERNAME` empty after the initial admin account has been created.
