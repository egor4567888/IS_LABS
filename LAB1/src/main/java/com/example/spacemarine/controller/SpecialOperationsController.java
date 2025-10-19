package com.example.spacemarine.controller;

import com.example.spacemarine.service.SpaceMarineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/special")
public class SpecialOperationsController {

    private final SpaceMarineService svc;

    @Autowired
    public SpecialOperationsController(SpaceMarineService svc) {
        this.svc = svc;
    }

    @GetMapping("/group-by-achievements")
    public List<Map<String, Object>> groupByAchievements() {
        return svc.groupByAchievements();
    }

    @GetMapping("/count-weapon-less-than")
    public ResponseEntity<Long> countWeaponLess(@RequestParam String weapon) {
        long cnt = svc.countWeaponTypeLessThan(weapon);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping("/count-health-greater-than")
    public ResponseEntity<Long> countHealthGreater(@RequestParam long threshold) {
        long cnt = svc.countHealthGreaterThan(threshold);
        return ResponseEntity.ok(cnt);
    }
}
