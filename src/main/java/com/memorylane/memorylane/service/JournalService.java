package com.memorylane.memorylane.service;

import com.memorylane.memorylane.model.Journal;
import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.repository.JournalRepository;
import com.memorylane.memorylane.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {
    private final JournalRepository journalRepository;
    private final UserRepository userRepository;

    public Journal create(String username, String title) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Journal journal = new Journal();
        journal.setUser(user);
        journal.setTitle(title);

        return journalRepository.save(journal);
    }

    public List<Journal> getMyJournals(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return journalRepository.findByUserId(user.getId());
    }

    public void delete(Long journalId, String username) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Ajanda bulunamadı"));

        if (!journal.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu ajandayı silme yetkiniz yok");
        }

        journalRepository.delete(journal);
    }
}
