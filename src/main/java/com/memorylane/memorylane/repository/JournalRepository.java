package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByUserId(Long userId);
    List<Journal> findByVisibility(Journal.Visibility visibility);
    List<Journal> findByVisibilityAndTitleContainingIgnoreCaseOrVisibilityAndUserUsernameContainingIgnoreCase(
            Journal.Visibility visibility1, String title,
            Journal.Visibility visibility2, String username
    );
    List<Journal> findByVisibilityOrderByCreatedAtDesc(Journal.Visibility visibility);
    List<Journal> findByVisibilityOrderBySaveCountDesc(Journal.Visibility visibility);
    List<Journal> findByVisibilityOrderByViewCountDesc(Journal.Visibility visibility);
}