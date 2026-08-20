package com.memorylane.memorylane.controller;

import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.service.UserService;
import com.memorylane.memorylane.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String q) {
        return ResponseEntity.ok(userService.searchUsers(q));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> toggleFollow(
            @AuthenticationPrincipal String currentUsername,
            @PathVariable String username) {
        boolean following = followService.toggleFollow(currentUsername, username);
        return ResponseEntity.ok(Map.of("following", following));
    }

    @GetMapping("/{username}/follow-status")
    public ResponseEntity<?> followStatus(
            @AuthenticationPrincipal String currentUsername,
            @PathVariable String username) {
        boolean following = followService.isFollowing(currentUsername, username);
        long followers = followService.getFollowerCount(username);
        long following2 = followService.getFollowingCount(username);
        return ResponseEntity.ok(Map.of(
                "following", following,
                "followerCount", followers,
                "followingCount", following2
        ));
    }
}