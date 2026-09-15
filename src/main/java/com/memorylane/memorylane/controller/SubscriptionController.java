package com.memorylane.memorylane.controller;

import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.service.JwtService;
import com.memorylane.memorylane.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        User user = userService.getProfile(username);

        return ResponseEntity.ok(Map.of(
                "plan", user.getSubscriptionPlan(),
                "isPlus", user.isPlus(),
                "isPro", user.isPro(),
                "journalLimit", user.getJournalLimit(),
                "pageLimit", user.getPageLimit()
        ));
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> event =
                    (Map<String, Object>) payload.get("event");
            String type = (String) event.get("type");
            String appUserId = (String) event.get("app_user_id");

            User user;
            try {
                user = userService.findByRevenueCatCustomerId(appUserId);
            } catch (RuntimeException e) {
                return ResponseEntity.ok().build();
            }

            switch (type) {
                case "INITIAL_PURCHASE":
                case "RENEWAL":
                case "PRODUCT_CHANGE": {
                    String productId = (String) event.get("product_id");
                    User.SubscriptionPlan plan =
                            productId.contains("pro")
                                    ? User.SubscriptionPlan.PRO
                                    : User.SubscriptionPlan.PLUS;
                    user.setSubscriptionPlan(plan);
                    user.setSubscriptionExpiresAt(
                            LocalDateTime.now().plusMonths(1));
                    break;
                }
                case "EXPIRATION":
                case "CANCELLATION": {
                    user.setSubscriptionPlan(User.SubscriptionPlan.FREE);
                    user.setSubscriptionExpiresAt(null);
                    break;
                }
            }
            userService.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register-customer")
    public ResponseEntity<?> registerCustomer(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        User user = userService.getProfile(username);
        user.setRevenueCatCustomerId(body.get("customerId"));
        userService.save(user);
        return ResponseEntity.ok(Map.of("message", "Customer ID registered"));
    }
}