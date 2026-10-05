package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.CodeforcesRatingChangeResponse;
import Competitive.Programming.Tracker.dto.CodeforcesUserResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionSummaryResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionCodeResponse;
import Competitive.Programming.Tracker.dto.LeetCodeProfileResponse;
import Competitive.Programming.Tracker.service.LeetCodeService;
import Competitive.Programming.Tracker.service.CodeforcesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platforms/codeforces")
public class CodeforcesController {

    private final CodeforcesService codeforcesService;
    private final LeetCodeService leetCodeService;

    public CodeforcesController(
            CodeforcesService codeforcesService,
            LeetCodeService leetCodeService) {
        this.codeforcesService = codeforcesService;
        this.leetCodeService = leetCodeService;
    }

    @GetMapping("/{handle}")
    public ResponseEntity<CodeforcesUserResponse> getUser(
            @PathVariable String handle) {
        return ResponseEntity.ok(codeforcesService.getUser(handle));
    }

    @GetMapping("/{handle}/submissions")
    public ResponseEntity<CodeforcesSubmissionSummaryResponse> getSubmissionSummary(
            @PathVariable String handle) {
        return ResponseEntity.ok(codeforcesService.getSubmissionSummary(handle));
    }

    @GetMapping("/{handle}/submissions/{submissionId}/code")
    public ResponseEntity<CodeforcesSubmissionCodeResponse> getSubmissionCode(
            @PathVariable String handle,
            @PathVariable Long submissionId,
            @RequestParam Long contestId,
            org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(
                codeforcesService.getSubmissionCode(
                        authentication.getName(), handle, contestId, submissionId));
    }

    @GetMapping("/{handle}/rating")
    public ResponseEntity<List<CodeforcesRatingChangeResponse>> getRatingHistory(
            @PathVariable String handle) {
        return ResponseEntity.ok(codeforcesService.getRatingHistory(handle));
    }
}
