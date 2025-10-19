package com.example.spacemarine.controller;

import com.example.spacemarine.entity.Chapter;
import com.example.spacemarine.service.ChapterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService svc;

    @Autowired
    public ChapterController(ChapterService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Chapter> list() {
        return svc.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chapter> get(@PathVariable Long id) {
        return svc.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Chapter> create(@Valid @RequestBody Chapter body) {
        Chapter created = svc.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> update(@PathVariable Long id, @Valid @RequestBody Chapter body) {
        try {
            Chapter updated = svc.update(id, body);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-via-db")
    public ResponseEntity<Long> createViaDb(@RequestParam String name, @RequestParam Long count) {
        Long id = svc.createChapterViaDb(name, count);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/dissolve-via-db/{id}")
    public ResponseEntity<Void> dissolveViaDb(@PathVariable Long id) {
        svc.dissolveChapterViaDb(id);
        return ResponseEntity.noContent().build();
    }
}
