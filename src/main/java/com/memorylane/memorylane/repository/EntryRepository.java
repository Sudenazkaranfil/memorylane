package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByJournalId(Long journalId);
}
