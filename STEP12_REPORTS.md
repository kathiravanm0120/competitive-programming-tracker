# Step 12 - Reports and Export

Implemented on top of Step 11.

## Features

- Report summary API
- PDF progress report
- Problems CSV export
- Goals CSV export
- Daily planner CSV export
- Platform statistics CSV export
- Achievements CSV export
- Protected report endpoints using the existing JWT authentication
- React `/reports` page
- Reports link in the sidebar

## APIs

- `GET /api/reports/summary`
- `GET /api/reports/export/pdf`
- `GET /api/reports/export/problems/csv`
- `GET /api/reports/export/goals/csv`
- `GET /api/reports/export/planner/csv`
- `GET /api/reports/export/platforms/csv`
- `GET /api/reports/export/achievements/csv`

## PDF

The backend uses Apache PDFBox 3.0.7 to generate the report. The generated PDF is kept ASCII-safe with the built-in Helvetica fonts so the report is portable without bundling a font file.

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

Open `http://localhost:5173/reports` after login.
