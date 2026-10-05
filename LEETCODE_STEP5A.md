# LeetCode Step 5A — Profile Statistics

This step adds a server-side LeetCode profile sync using the public `https://leetcode.com/graphql` endpoint.

## Backend endpoint

`GET /api/platforms/leetcode/{username}`

It returns:
- username
- global ranking
- avatar / public profile fields
- total solved
- Easy / Medium / Hard solved counts
- accepted submissions
- total submissions
- calculated acceptance rate

## Frontend

The Profile page now has a **Sync LeetCode** button in the LeetCode platform card. After a successful sync it displays the live statistics returned by LeetCode.

## Important limitation

LeetCode's GraphQL endpoint is an internal/publicly reachable web API surface rather than a stable, versioned developer API with the same guarantees as Codeforces' documented API. Its schema or anti-bot behavior can change. This project therefore treats a failed GraphQL request as a sync error rather than silently using stale/mock values.

The query shape used here is based on the public profile/session-progress GraphQL fields currently used by LeetCode pages and documented in community examples.
