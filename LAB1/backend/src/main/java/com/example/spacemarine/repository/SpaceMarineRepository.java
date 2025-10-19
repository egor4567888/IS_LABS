package com.example.spacemarine.repository;

import com.example.spacemarine.entity.SpaceMarine;
import com.example.spacemarine.entity.Weapon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceMarineRepository extends JpaRepository<SpaceMarine, Long> {
    Page<SpaceMarine> findByName(String name, Pageable pageable);
    Page<SpaceMarine> findByAchievements(String achievements, Pageable pageable);
    long countByWeaponType(Weapon weapon);
    long countByHealthGreaterThan(Long health);
}
