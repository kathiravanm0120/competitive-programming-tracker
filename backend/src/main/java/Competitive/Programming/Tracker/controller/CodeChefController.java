package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.CodeChefProfileResponse;
import Competitive.Programming.Tracker.service.CodeChefService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platforms/codechef")
public class CodeChefController {

    private final CodeChefService codeChefService;

    public CodeChefController(CodeChefService codeChefService) {
        this.codeChefService = codeChefService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<CodeChefProfileResponse> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(codeChefService.getProfile(username));
    }
}
