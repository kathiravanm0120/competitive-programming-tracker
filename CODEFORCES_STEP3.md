# Codeforces Step 3 - Submission & Solved Analytics

Added live Codeforces `user.status` integration.

## Backend
- `GET /api/platforms/codeforces/{handle}/submissions`
- Fetches up to the latest 1000 submissions from Codeforces.
- Returns synced submission count, accepted submission count, unique solved count within the synced set, verdict breakdown, language breakdown, and the 25 most recent submissions.

## Frontend
On the Profile page, the Codeforces card now includes **Sync Submissions**.
It displays:
- Synced submissions
- Accepted submissions
- Unique solved problems in the synced set
- Verdict breakdown
- Language breakdown
- Recent submissions table with problem, date, verdict, language, time and memory

## Important limitation
This step intentionally syncs the latest 1000 Codeforces submissions because that is the maximum amount returned by one `user.status` request. The "Unique Solved" number is therefore the number of unique accepted problems found in that synced set, not necessarily the user's lifetime total.

The project is ready for the next step: a paginated/full-history synchronization strategy if lifetime exact solved counts are required.
