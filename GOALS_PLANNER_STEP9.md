# Step 9 — Goals & Daily Planner

This step adds user-owned goals and daily planner tasks.

## Backend APIs

### Goals
- `GET /api/goals`
- `POST /api/goals`
- `PUT /api/goals/{id}`
- `DELETE /api/goals/{id}`

### Planner
- `GET /api/planner/tasks?date=YYYY-MM-DD`
- `GET /api/planner/tasks`
- `POST /api/planner/tasks`
- `PUT /api/planner/tasks/{id}`
- `DELETE /api/planner/tasks/{id}`

All endpoints require the existing JWT authentication.

## Goal behavior

- Target must be greater than zero.
- Current progress defaults to zero.
- End date cannot be before start date.
- Progress is capped at 100%.
- Status is derived as `COMPLETED`, `OVERDUE`, `ACTIVE`, `PAUSED`, or `CANCELLED`.
- A planner task can optionally be linked to a user's goal.

## Frontend routes

- `/goals`
- `/planner`

Hibernate/JPA will create the new `goals` and `planner_tasks` tables automatically when the application starts because the project uses `spring.jpa.hibernate.ddl-auto=update`.
