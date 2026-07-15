package com.memorylane.memorylane.controller;

import com.memorylane.memorylane.model.Journal;
import com.memorylane.memorylane.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @PostMapping
    public ResponseEntity<Journal> create(
            @AuthenticationPrincipal String username,
            @RequestBody Map<String, String> body) {
        Journal journal = journalService.create(username, body.get("title"));
        return ResponseEntity.ok(journal);
    }

    @GetMapping
    public ResponseEntity<List<Journal>> getMyJournals(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(journalService.getMyJournals(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        journalService.delete(id, username);
        return ResponseEntity.ok("Ajanda silindi");
    }
}
