# Dashboard Step 4

The dashboard now uses real backend data instead of the previous mock dashboard values.

## Backend

New endpoint:

- `GET /api/dashboard`

The endpoint requires the normal JWT authentication and returns:

- local tracked-problem statistics
- Codeforces connection status
- current and maximum rating
- rank and max rank
- contribution
- contest count
- solved-problem count from the synced submission window
- accepted submission count
- verdict statistics
- language statistics
- recent Codeforces submissions
- the latest 30 Codeforces rating points

The dashboard service uses the Codeforces account saved in `platform_accounts`. If there is no saved Codeforces account, the endpoint still returns the local tracker statistics and reports Codeforces as disconnected.

A 60-second in-memory cache is used for Codeforces dashboard data. External Codeforces requests are spaced slightly over two seconds apart to respect the documented API request rate limit.

## Frontend

`Dashboard.jsx` now renders:

- live Codeforces rating cards
- Codeforces contest count
- solved/submission metrics
- inline SVG rating history graph
- verdict breakdown
- language breakdown
- recent Codeforces submissions
- local tracked-problem progress
- recent tracked problems

No additional chart library is required.

## Run

Backend:

```powershell
cd D:\projects\competitive-programming-tracker\backend
.\mvnw.cmd clean
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd D:\projects\competitive-programming-tracker\frontend
npm install
npm run dev
```

Then open `http://localhost:5173/` after logging in.
