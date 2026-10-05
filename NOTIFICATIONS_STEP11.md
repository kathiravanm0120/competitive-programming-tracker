# Step 11 — Notifications

Implemented a functional in-app notification system.

## Features

- Notification persistence in PostgreSQL
- Unread count endpoint
- Mark one notification as read
- Mark all notifications as read
- Delete notification
- Navbar notification bell with unread badge
- Notification dropdown
- Dedicated `/notifications` page
- Achievement-unlocked notifications
- Goal 80% progress notification
- Goal-completed notification
- Planner task-completed notification
- Duplicate protection via reference keys

## API

- `GET /api/notifications`
- `GET /api/notifications/unread-count`
- `PUT /api/notifications/{id}/read`
- `PUT /api/notifications/read-all`
- `DELETE /api/notifications/{id}`

## Notes

Notifications are user-owned and protected by JWT authentication. Hibernate creates the `notifications` table automatically.
