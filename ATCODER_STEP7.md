# AtCoder Step 7

Implemented AtCoder public-profile synchronization.

## Backend
- `GET /api/platforms/atcoder/{username}`
- Parses the public AtCoder user profile page.
- Returns username, current rating, highest rating, rank, rated matches, last competed date, country, affiliation, and win count.

## Frontend
- AtCoder platform card now has a `Sync AtCoder` button.
- Synced profile metrics appear in the card.

## Current AtCoder limitation
AtCoder's public profile page exposes contest-status fields such as rank, rating, highest rating, rated matches, and last competed. The current implementation does not claim a solved-problem count because that value is not present on the public profile page used by this integration.
