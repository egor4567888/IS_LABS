package com.example.spacemarine.controller;

import com.example.spacemarine.entity.SpaceMarine;
import com.example.spacemarine.service.SpaceMarineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/space-marines")
public class SpaceMarineController {

    private final SpaceMarineService svc;

    @Autowired
    public SpaceMarineController(SpaceMarineService svc) {
        this.svc = svc;
    }

    @GetMapping
    public Page<SpaceMarine> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String achievements
    ) {
        return svc.list(page, size, sortBy, name, achievements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceMarine> get(@PathVariable Long id) {
        return svc.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SpaceMarine> create(@Valid @RequestBody SpaceMarine body) {
        SpaceMarine created = svc.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceMarine> update(@PathVariable Long id, @Valid @RequestBody SpaceMarine body) {
        try {
            SpaceMarine updated = svc.update(id, body);
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
}
