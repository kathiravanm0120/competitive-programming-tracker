package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.AtCoderProfileResponse;
import Competitive.Programming.Tracker.service.AtCoderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platforms/atcoder")
public class AtCoderController {

    private final AtCoderService atCoderService;

    public AtCoderController(AtCoderService atCoderService) {
        this.atCoderService = atCoderService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<AtCoderProfileResponse> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(atCoderService.getProfile(username));
    }
}
