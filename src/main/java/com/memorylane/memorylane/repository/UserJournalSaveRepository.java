package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Journal;
import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.model.UserJournalSave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserJournalSaveRepository extends JpaRepository<UserJournalSave, Long> {
    boolean existsByUserAndJournal(User user, Journal journal);
    Optional<UserJournalSave> findByUserAndJournal(User user, Journal journal);
    List<UserJournalSave> findByUser(User user);
    long countByJournal(Journal journal);
}