# Step 14 Release Checklist

## Backend

- [x] JWT filter returns structured 401 responses
- [x] `/api/admin/**` has explicit `ADMIN` authorization
- [x] Admin controller keeps method-level authorization
- [x] Self-disable/self-demotion is blocked
- [x] Auth request validation added
- [x] Environment-driven secrets/configuration
- [x] Validation / forbidden / unauthorized error handling
- [x] Integration tests for public, authenticated, user, admin, and disabled-user flows

## Frontend

- [x] Admin route remains protected in the UI
- [x] API client clears invalid sessions on HTTP 401
- [x] Role is refreshed from `/api/auth/me`

## Local verification

1. Start PostgreSQL.
2. Start backend with `cd backend` then `\mvnw.cmd spring-boot:run`.
3. Start frontend with `cd frontend` then `npm install` and `npm run dev`.
4. Run backend tests with `\mvnw.cmd clean test`.
5. Manually verify the security checklist in `STEP14_TESTING_SECURITY.md`.
