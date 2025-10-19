package com.example.spacemarine.controller;

import com.example.spacemarine.dto.ChapterDto;
import com.example.spacemarine.entity.Chapter;
import com.example.spacemarine.mapper.EntityMapper;
import com.example.spacemarine.service.ChapterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService svc;

    @Autowired
    public ChapterController(ChapterService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<ChapterDto> list() {
        return svc.listAll().stream()
                .map(EntityMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterDto> get(@PathVariable Long id) {
        return svc.get(id)
                .map(EntityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChapterDto> create(@Valid @RequestBody ChapterDto dto) {
        Chapter c = EntityMapper.fromDto(dto);
        Chapter created = svc.create(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityMapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChapterDto> update(@PathVariable Long id, @Valid @RequestBody ChapterDto dto) {
        try {
            Chapter updated = svc.update(id, EntityMapper.fromDto(dto));
            return ResponseEntity.ok(EntityMapper.toDto(updated));
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
