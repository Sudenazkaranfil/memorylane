package com.seyahood.controller;

import com.seyahood.model.User;
import com.seyahood.service.LocationEstimationService;
import com.seyahood.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
public class LocationEstimationController {

    private final LocationEstimationService locationEstimationService;
    private final UserService userService;

    @PostMapping("/estimate-location")
    public ResponseEntity<?> estimateLocation(
            @AuthenticationPrincipal String username,
            @RequestParam("file") MultipartFile file) {
        User user = userService.getProfile(username);
        if (!user.isPro()) {
            return ResponseEntity.status(403).body(Map.of("error", "PRO_REQUIRED"));
        }
        try {
            String locationName = locationEstimationService.estimateLocation(
                    file.getBytes(), file.getContentType());
            return ResponseEntity.ok(Map.of("locationName", locationName));
        } catch (Exception e) {
            return ResponseEntity.status(502).body(Map.of("error", "ESTIMATION_FAILED"));
        }
    }
}
