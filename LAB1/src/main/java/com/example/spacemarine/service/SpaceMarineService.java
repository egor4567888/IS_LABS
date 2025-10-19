package com.example.spacemarine.service;

import com.example.spacemarine.entity.SpaceMarine;
import com.example.spacemarine.entity.Weapon;
import com.example.spacemarine.repository.SpaceMarineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SpaceMarineService {

    private final SpaceMarineRepository repo;
    private final SimpMessagingTemplate messaging;
    private final JdbcTemplate jdbc;

    @Autowired
    public SpaceMarineService(SpaceMarineRepository repo, SimpMessagingTemplate messaging, JdbcTemplate jdbc) {
        this.repo = repo;
        this.messaging = messaging;
        this.jdbc = jdbc;
    }

    public Page<SpaceMarine> list(int page, int size, String sortBy, String nameFilter, String achievementsFilter) {
        Sort sort = Sort.by(sortBy == null || sortBy.isBlank() ? "id" : sortBy);
        Pageable p = PageRequest.of(page, size, sort);
        if (nameFilter != null && !nameFilter.isBlank()) {
            return repo.findByName(nameFilter, p);
        }
        if (achievementsFilter != null && !achievementsFilter.isBlank()) {
            return repo.findByAchievements(achievementsFilter, p);
        }
        return repo.findAll(p);
    }

    public Optional<SpaceMarine> get(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public SpaceMarine create(SpaceMarine sm) {
        SpaceMarine saved = repo.save(sm);
        notifyClients("create", saved.getId());
        return saved;
    }

    @Transactional
    public SpaceMarine update(Long id, SpaceMarine input) {
        SpaceMarine existing = repo.findById(id).orElseThrow(() -> new NoSuchElementException("SpaceMarine not found"));
        existing.setName(input.getName());
        existing.setCoordinates(input.getCoordinates());
        existing.setHealth(input.getHealth());
        existing.setAchievements(input.getAchievements());
        existing.setHeight(input.getHeight());
        existing.setWeaponType(input.getWeaponType());
        existing.setChapter(input.getChapter());
        SpaceMarine saved = repo.save(existing);
        notifyClients("update", saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            notifyClients("delete", id);
        }
    }

    private void notifyClients(String action, Long id) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", action);
        payload.put("id", id);
        messaging.convertAndSend("/topic/spaceMarines", payload);
    }

    // Special operations (simple implementations)
    public List<Map<String, Object>> groupByAchievements() {
        // Try DB group (works in H2 view too)
        try {
            return jdbc.queryForList("SELECT achievements, COUNT(*) AS cnt FROM space_marines GROUP BY achievements");
        } catch (Exception ex) {
            // fallback empty
            return Collections.emptyList();
        }
    }

    public long countWeaponTypeLessThan(String weaponName) {
        if (weaponName == null) return 0;
        // interpret "less than" by enum ordinal defined in Weapon enum
        try {
            Weapon target = Weapon.valueOf(weaponName);
            int targetOrd = target.ordinal();
            long count = 0;
            for (Weapon w : Weapon.values()) {
                if (w.ordinal() < targetOrd) {
                    count += repo.countByWeaponType(w);
                }
            }
            return count;
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }

    public long countHealthGreaterThan(long threshold) {
        return repo.countByHealthGreaterThan(threshold);
    }
}
