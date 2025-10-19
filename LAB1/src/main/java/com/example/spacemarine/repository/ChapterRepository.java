package com.example.spacemarine.repository;

import com.example.spacemarine.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findByName(String name);
}
