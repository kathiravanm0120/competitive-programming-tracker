# Admin Panel Setup

The application now supports USER and ADMIN roles.

## Promote an existing user to ADMIN

1. Open `backend/src/main/resources/application.properties`.
2. Add:

```properties
app.admin.bootstrap-username=testuser
```

3. Restart the backend once.
4. The existing `testuser` account will be promoted to ADMIN.
5. Remove or comment the property after promotion if you do not want it re-applied on future starts.

The application never stores a default admin password and never auto-promotes arbitrary users.

## Admin routes

- `/admin`
- `/admin/users`
- `/admin/problems`

Backend endpoints under `/api/admin/**` require `ROLE_ADMIN`.
