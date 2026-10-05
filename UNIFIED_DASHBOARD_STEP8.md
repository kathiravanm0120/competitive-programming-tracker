# Step 8 - Unified Multi-Platform Dashboard

Implemented on top of Step 7.

## Backend

- Added `PlatformStatistics` normalized DTO.
- `/api/dashboard` now combines Codeforces, LeetCode, CodeChef, and AtCoder when accounts are saved.
- Added `overall` statistics: connected platforms, available platforms, total reported solved count, total contests, rated platform count, and local tracker counts.
- Each platform has `connected`, `available`, `handle`, profile URL, rating/max rating/rank where applicable, solved/contest counts where available, plus platform-specific `metrics`.
- Platform failures are isolated: one unavailable platform no longer makes the entire dashboard fail.
- Unified dashboard data is cached for 60 seconds per user.
- Existing detailed Codeforces dashboard data remains available for rating/submission widgets.

## Frontend

- Replaced mock/mixed dashboard layout with a unified multi-platform dashboard.
- Added four platform cards.
- Added overall summary cards.
- Added platform health section.
- Kept Codeforces rating graph, verdict breakdown, and tracked problems.

## Data note

`overall.totalSolved` is the sum of the solved values reported by each platform. It is **not a globally deduplicated problem count** because the same problem can exist on multiple platforms. AtCoder currently does not supply a solved count through this profile integration, so its solved value is blank.
