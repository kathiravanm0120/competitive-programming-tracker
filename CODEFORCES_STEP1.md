# Codeforces Integration — Step 1

This version adds a live Codeforces profile lookup.

## Backend endpoint

```text
GET http://localhost:8080/api/platforms/codeforces/{handle}
```

The endpoint calls the official Codeforces `user.info` API and returns the public user profile. The Codeforces API documents `user.info` as the method for fetching one or more users by handle; public API calls do not require authentication. API calls are currently limited to at most one request every two seconds.

## Frontend

Open:

```text
http://localhost:5173/profile
```

Under **Coding Platform Accounts**:

1. Enter a Codeforces handle.
2. Click **Save Account**.
3. Click **Sync Codeforces**.
4. The live rating, max rating, rank, max rank, contribution, and handle will appear.

## Postman test

After logging in and receiving a JWT, send:

```text
GET http://localhost:8080/api/platforms/codeforces/tourist
Authorization: Bearer <JWT>
```

A successful response contains fields such as `handle`, `rating`, `maxRating`, `rank`, `maxRank`, `avatar`, and `titlePhoto`.
