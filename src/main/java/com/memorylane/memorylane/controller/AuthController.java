package com.memorylane.memorylane.controller;

import com.memorylane.memorylane.dto.LoginRequest;
import com.memorylane.memorylane.dto.RegisterRequest;
import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.service.EmailService;
import com.memorylane.memorylane.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(Map.of("message", "Doğrulama kodu e-posta adresinize gönderildi"));
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

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        User user = userService.findByEmail(email);

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Geçersiz kod"));
        }

        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Kodun süresi dolmuş"));
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userService.save(user);

        return ResponseEntity.ok(Map.of("message", "E-posta doğrulandı!"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        User user = userService.findByEmail(email);

        if (user.getVerified()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Hesap zaten doğrulanmış"));
        }

        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(10));
        userService.save(user);
        emailService.sendVerificationCode(email, code);

        return ResponseEntity.ok(Map.of("message", "Kod gönderildi"));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        User user = userService.findByEmail(email);

        if (user.getResetPasswordCode() == null || !user.getResetPasswordCode().equals(code)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Geçersiz kod"));
        }

        if (user.getResetPasswordCodeExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Kodun süresi dolmuş"));
        }

        return ResponseEntity.ok(Map.of("message", "Kod doğrulandı"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            userService.sendResetCode(body.get("email"));
            return ResponseEntity.ok(Map.of("message", "Şifre sıfırlama kodu gönderildi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            userService.resetPassword(body.get("email"), body.get("code"), body.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla güncellendi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}