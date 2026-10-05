# Step 10 - Achievements + Codeforces Submitted Code Viewer

## Added

- `GET /api/achievements` for live achievement progress.
- Achievement rules for tracked solved problems, goals, planner tasks/streak, connected platforms, and Codeforces rating milestones.
- `/achievements` frontend page.
- Achievements added to the sidebar.
- Codeforces submission table now has a **View Code** button.
- `GET /api/platforms/codeforces/{handle}/submissions/{submissionId}/code` fetches the public Codeforces submission page and displays the source in a modal.
- Every code modal includes **Open on Codeforces** as a fallback.

## Codeforces source limitation

Codeforces documents that `user.status` can return source code with `includeSources=true` only when requested for the caller's own account. This project does not store Codeforces API credentials, so the website uses the public submission page for the Codeforces-only code viewer. If Codeforces blocks that server-side fetch or does not expose source for a submission, the user can still open the original submission on Codeforces.

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

Routes:

- `/achievements`
- `/profile` for the Codeforces submission code viewer.
