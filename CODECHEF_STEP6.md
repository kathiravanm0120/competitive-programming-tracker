# CodeChef Step 6 — Profile Statistics

This step adds a server-side CodeChef public-profile integration. CodeChef does not expose a general public API for these profile statistics, so the backend reads the public profile page `https://www.codechef.com/users/{username}` and parses the visible profile data.

## Endpoint

`GET /api/platforms/codechef/{username}`

## Returned statistics

- Username
- Current rating
- Highest rating
- Stars
- Global rank
- Country rank
- Contests participated
- Problems solved
- Profile URL

## Frontend

Profile → CodeChef → Save Account → Sync CodeChef

## Limitation

This integration depends on the public CodeChef profile HTML structure. If CodeChef changes that page layout, the parser may need an update. The user profile itself is public and exposes rating, contest count, ranks, and total problems solved on the current site.
