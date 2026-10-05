# Competitive Programming Tracker - Profile + Codeforces

This version includes:
- JWT authentication
- Login/Register
- PostgreSQL persistence
- Problem CRUD
- Profile management
- Platform account management
- Codeforces user.info integration
- Codeforces profile sync from the Profile page

## Backend

Edit:
`backend/src/main/resources/application.properties`

Set your PostgreSQL password:
`spring.datasource.password=YOUR_POSTGRES_PASSWORD`

Then run:
```powershell
cd backend
mvn spring-boot:run
```

## Frontend

From another terminal:
```powershell
cd frontend
npm install
npm run dev
```

Open:
`http://localhost:5173`

Profile page:
`http://localhost:5173/profile`

## Codeforces API test

After logging in, you can test:
`GET http://localhost:8080/api/platforms/codeforces/<handle>`

The Codeforces integration uses the public `user.info` API.
