package com.memorylane.memorylane.service;

import com.memorylane.memorylane.model.Follow;
import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.repository.FollowRepository;
import com.memorylane.memorylane.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public boolean toggleFollow(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            followRepository.findByFollowerAndFollowing(follower, following)
                    .ifPresent(followRepository::delete);
            return false;
        } else {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.save(follow);
            return true;
        }
    }

    public boolean isFollowing(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    public long getFollowerCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return followRepository.countByFollowing(user);
    }

    public long getFollowingCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return followRepository.countByFollower(user);
    }
}