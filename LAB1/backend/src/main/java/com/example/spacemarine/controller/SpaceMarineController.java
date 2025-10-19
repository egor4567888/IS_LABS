package com.example.spacemarine.controller;

import com.example.spacemarine.dto.SpaceMarineDto;
import com.example.spacemarine.entity.Chapter;
import com.example.spacemarine.entity.SpaceMarine;
import com.example.spacemarine.mapper.EntityMapper;
import com.example.spacemarine.service.ChapterService;
import com.example.spacemarine.service.SpaceMarineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/space-marines")
public class SpaceMarineController {

    private final SpaceMarineService svc;
    private final ChapterService chapterService;

    @Autowired
    public SpaceMarineController(SpaceMarineService svc, ChapterService chapterService) {
        this.svc = svc;
        this.chapterService = chapterService;
    }

    /**
     * List with pagination, optional exact-match filters for name or achievements and optional sortBy.
     * Example: /api/space-marines?page=0&size=20&sortBy=name&name=Igor
     */
    @GetMapping
    public Page<SpaceMarineDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String achievements
    ) {
        Page<SpaceMarine> entities = svc.list(page, size, sortBy, name, achievements);
        var dtos = entities.stream()
                .map(EntityMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, entities.getPageable(), entities.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceMarineDto> get(@PathVariable Long id) {
        return svc.get(id)
                .map(EntityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new SpaceMarine.
     * Body: SpaceMarineDto with chapterId set to an existing chapter id.
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SpaceMarineDto body) {
        if (body.getChapterId() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "chapterId must be provided and refer to an existing Chapter"));
        }

        Chapter chapter = chapterService.get(body.getChapterId()).orElse(null);
        if (chapter == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chapter with id " + body.getChapterId() + " not found"));
        }

        SpaceMarine entity = EntityMapper.fromDto(body, chapter);
        SpaceMarine created = svc.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityMapper.toDto(created));
    }

    /**
     * Update existing SpaceMarine by id.
     * Body: SpaceMarineDto (chapterId required).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SpaceMarineDto body) {
        try {
            if (body.getChapterId() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "chapterId must be provided and refer to an existing Chapter"));
            }

            Chapter chapter = chapterService.get(body.getChapterId()).orElse(null);
            if (chapter == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chapter with id " + body.getChapterId() + " not found"));
            }

            SpaceMarine incoming = EntityMapper.fromDto(body, chapter);
            SpaceMarine updated = svc.update(id, incoming);
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
}
