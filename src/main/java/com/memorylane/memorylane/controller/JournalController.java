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
        Journal journal = journalService.create(username, body.get("title"), body.get("visibility"));
        return ResponseEntity.ok(journal);
    }

    @GetMapping
    public ResponseEntity<List<Journal>> getMyJournals(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(journalService.getMyJournals(username));
    }

    @GetMapping("/public")
    public ResponseEntity<List<Journal>> getPublicJournals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(journalService.getPublicJournals(search, sortBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        journalService.delete(id, username);
        return ResponseEntity.ok("Ajanda silindi");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Journal> update(
            @AuthenticationPrincipal String username,
            @PathVariable Long id,
            @RequestBody Journal journalData) {
        Journal journal = journalService.update(id, username, journalData);
        return ResponseEntity.ok(journal);
    }
}