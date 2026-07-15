package com.memorylane.memorylane.service;

import com.memorylane.memorylane.model.Entry;
import com.memorylane.memorylane.model.Journal;
import com.memorylane.memorylane.repository.EntryRepository;
import com.memorylane.memorylane.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntryService {
    private final EntryRepository entryRepository;
    private final JournalRepository journalRepository;

    public Entry create(Long journalId, String username, Entry entryData) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Ajanda bulunamadı"));

        if(!journal.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu ajandaya erişim yetkiniz yok");
        }

        entryData.setJournal(journal);
        return entryRepository.save(entryData);
    }

    public List<Entry> getEntries(Long journalId, String username) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Ajanda bulunamadı"));

        if(!journal.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu ajandaya erişim yetkiniz yok");
        }

        return entryRepository.findByJournalId(journalId);
    }

    public void delete(Long entryId, String username) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Giriş bulunamadı"));

        if (!entry.getJournal().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu girişi silme yetkiniz yok");
        }

        entryRepository.delete(entry);
    }

    public Entry addPhotoToEntry(Long entryId, String username, String imageUrl) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Giriş bulunamadı"));

        if(!entry.getJournal().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu girişe erişim yetkiniz yok");
        }

        entry.getPhotoUrls().add(imageUrl);
        return entryRepository.save(entry);
    }
}
