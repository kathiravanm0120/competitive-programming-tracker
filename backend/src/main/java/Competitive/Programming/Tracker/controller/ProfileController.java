package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.PlatformAccountRequest;
import Competitive.Programming.Tracker.dto.PlatformAccountResponse;
import Competitive.Programming.Tracker.dto.ProfileUpdateRequest;
import Competitive.Programming.Tracker.dto.UserResponse;
import Competitive.Programming.Tracker.service.PlatformAccountService;
import Competitive.Programming.Tracker.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final PlatformAccountService platformAccountService;

    public ProfileController(
            ProfileService profileService,
            PlatformAccountService platformAccountService) {
        this.profileService = profileService;
        this.platformAccountService = platformAccountService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                profileService.getProfile(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                profileService.updateProfile(
                        authentication.getName(), request));
    }

    @GetMapping("/platforms")
    public ResponseEntity<List<PlatformAccountResponse>> getPlatformAccounts(
            Authentication authentication) {

        return ResponseEntity.ok(
                platformAccountService.getAccounts(authentication.getName()));
    }

    @PutMapping("/platforms")
    public ResponseEntity<PlatformAccountResponse> savePlatformAccount(
            @RequestBody PlatformAccountRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                platformAccountService.saveAccount(
                        authentication.getName(), request));
    }

    @DeleteMapping("/platforms/{platform}")
    public ResponseEntity<Void> deletePlatformAccount(
            @PathVariable String platform,
            Authentication authentication) {

        platformAccountService.deleteAccount(
                authentication.getName(), platform);

        return ResponseEntity.noContent().build();
    }
}
