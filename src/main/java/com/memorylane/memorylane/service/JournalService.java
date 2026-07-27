package com.memorylane.memorylane.service;

import com.memorylane.memorylane.model.Journal;
import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.repository.EntryRepository;
import com.memorylane.memorylane.repository.JournalRepository;
import com.memorylane.memorylane.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {
    private final JournalRepository journalRepository;
    private final UserRepository userRepository;
    private final EntryRepository entryRepository;

    public Journal create(String username, String title, String visibility) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Journal journal = new Journal();
        journal.setUser(user);
        journal.setTitle(title);
        if (visibility != null) {
            journal.setVisibility(Journal.Visibility.valueOf(visibility));
        }

        return journalRepository.save(journal);
    }

    public List<Journal> getMyJournals(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return journalRepository.findByUserId(user.getId());
    }

    public List<Journal> getPublicJournals(String search, String sortBy) {
        List<Journal> journals;

        if (search != null && !search.isEmpty()) {
            journals = journalRepository
                    .findByVisibilityAndTitleContainingIgnoreCaseOrVisibilityAndUserUsernameContainingIgnoreCase(
                            Journal.Visibility.PUBLIC, search,
                            Journal.Visibility.PUBLIC, search
                    );
        } else {
            journals = journalRepository.findByVisibility(Journal.Visibility.PUBLIC);
        }

        if ("most_pages".equals(sortBy)) {
            journals.sort((a, b) -> b.getEntries().size() - a.getEntries().size());
        } else if ("popular".equals(sortBy)) {
            journals.sort((a, b) -> b.getEntries().size() - a.getEntries().size());
        } else {
            journals.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        return journals;
    }

    @Transactional
    public void delete(Long journalId, String username) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Ajanda bulunamadı"));

        if (!journal.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu ajandayı silme yetkiniz yok");
        }

        entryRepository.deleteByJournalId(journalId);
        journalRepository.delete(journal);
    }

    public Journal update(Long id, String username, Journal journalData) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ajanda bulunamadı"));

        if (!journal.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu ajandayı düzenleme yetkiniz yok");
        }

        if (journalData.getTitle() != null) journal.setTitle(journalData.getTitle());
        if (journalData.getVisibility() != null) journal.setVisibility(journalData.getVisibility());

        return journalRepository.save(journal);
    }
}