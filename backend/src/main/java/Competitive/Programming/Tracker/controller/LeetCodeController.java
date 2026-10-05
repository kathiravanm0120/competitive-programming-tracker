package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.LeetCodeProfileResponse;
import Competitive.Programming.Tracker.service.LeetCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platforms/leetcode")
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    public LeetCodeController(LeetCodeService leetCodeService) {
        this.leetCodeService = leetCodeService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<LeetCodeProfileResponse> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(leetCodeService.getProfile(username));
    }
}
