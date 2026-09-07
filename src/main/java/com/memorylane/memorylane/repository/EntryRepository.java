package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByJournalIdOrderByCreatedAtAsc(Long journalId);
    void deleteByJournalId(Long journalId);

    @Query("SELECT e FROM Entry e WHERE e.lat IS NOT NULL AND e.lng IS NOT NULL AND e.journal.visibility = 'PUBLIC'")
    List<Entry> findPublicLocations();
}
