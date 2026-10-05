# Security Notes

## Local development

1. Copy `backend/.env.example` to your own environment configuration or set the variables in PowerShell.
2. Do not commit passwords, JWT secrets, real API credentials, or production URLs.
3. Keep `ADMIN_BOOTSTRAP_USERNAME` empty after initial admin creation.

## Environment variables

The backend reads database credentials, JWT secret, CORS origins, server port, and admin bootstrap configuration from environment variables. Development fallbacks remain in `application.properties` so the current local setup still works.

## Authentication

- JWTs are signed with HS256.
- Missing, malformed, tampered, and expired tokens return 401.
- Disabled users cannot authenticate even with an otherwise valid JWT.
- The API is stateless; server sessions are not used.

## Authorization

- `/api/admin/**` requires `ROLE_ADMIN`.
- Method-level `@PreAuthorize` also protects the admin controller.
- Users cannot disable or remove the admin role from their own admin account.
- User-owned resources are checked by the authenticated username before update/delete.

## Error handling

Validation failures return 400 with field-level details. Authentication failures return 401. Authorization failures return 403. Unexpected server errors return a generic 500 response instead of exposing internal exception messages.

## Testing

Run:

```powershell
cd backend
.\mvnw.cmd clean test
```

See `STEP14_TESTING_SECURITY.md` for the full checklist.
