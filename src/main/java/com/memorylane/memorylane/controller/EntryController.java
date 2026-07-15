package com.memorylane.memorylane.controller;

import com.memorylane.memorylane.model.Entry;
import com.memorylane.memorylane.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/journals/{journalId}/entries")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;

    @PostMapping
    public ResponseEntity<Entry> create(
            @AuthenticationPrincipal String username,
            @PathVariable Long journalId,
            @RequestBody Entry entry) {
        return ResponseEntity.ok(entryService.create(journalId, username, entry));
    }

    @GetMapping
    public ResponseEntity<List<Entry>> getEntries(
            @AuthenticationPrincipal String username,
            @PathVariable Long journalId) {
        return ResponseEntity.ok(entryService.getEntries(journalId, username));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal String username,
            @PathVariable Long entryId) {
        entryService.delete(entryId, username);
        return ResponseEntity.ok("Giriş silindi");
    }
}
