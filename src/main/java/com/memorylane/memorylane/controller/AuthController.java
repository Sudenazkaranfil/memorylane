package com.memorylane.memorylane.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.memorylane.memorylane.dto.LoginRequest;
import com.memorylane.memorylane.dto.RegisterRequest;
import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        return  ResponseEntity.ok("Kayıt başarılı! Kullanıcı: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getEmail(), request.getPassword());
        User user = userService.findByEmail(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername()
        ));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal String username,
            @RequestBody Map<String, String> body) {
        User user = userService.updateProfile(
                username,
                body.get("firstName"),
                body.get("lastName"),
                body.get("bio"),
                body.get("profileImageUrl"),
                body.get("location"),
                body.get("website"),
                body.get("favoriteDestination")
        );
        return ResponseEntity.ok(user);
    }
}
