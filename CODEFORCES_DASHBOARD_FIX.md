# Codeforces dashboard fix

Fixed a runtime parsing bug in `CodeforcesService.getSubmissionSummary()`.

Codeforces problem indexes are strings such as `A`, `B`, `C`, etc. The previous implementation attempted `asInt()` on `problem.index`, which caused errors such as:

`StringNode method 'asInt()' cannot coerce value 'B' to int`

The submission DTO now stores `problemIndex` as a `String`, and the service reads it with `asText()`. This allows the Codeforces dashboard/profile submission synchronization to process normal submissions correctly.
