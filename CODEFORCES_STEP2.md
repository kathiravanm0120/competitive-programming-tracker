# Codeforces Step 2 - Contest History and Rating Graph

Added:

- `GET /api/platforms/codeforces/{handle}/rating`
- Codeforces `user.rating` integration
- Contest rating history DTO
- Profile page contest-history sync
- Rating history SVG graph
- Recent contest table with contest link, date, rank, rating change, and new rating

The API uses Codeforces' public `user.rating` method, which returns a list of `RatingChange` objects for a user. Codeforces documents `contestId`, `contestName`, `rank`, `ratingUpdateTimeSeconds`, `oldRating`, and `newRating` on that object.

No API key is required for this public data.
