package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Follow;
import com.memorylane.memorylane.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollowing(User following);
    List<Follow> findByFollower(User follower);
    long countByFollowing(User following);
    long countByFollower(User follower);
}