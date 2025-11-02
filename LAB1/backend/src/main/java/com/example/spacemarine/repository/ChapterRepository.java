package com.example.spacemarine.repository;

import com.example.spacemarine.entity.Chapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ChapterRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Chapter> findById(Long id) {
        try {
            Chapter chapter = entityManager.find(Chapter.class, id);
            return Optional.ofNullable(chapter);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Chapter> findAll() {
        return entityManager.createQuery("FROM Chapter", Chapter.class)
                .getResultList();
    }

    public Optional<Chapter> findByName(String name) {
        try {
            Chapter chapter = entityManager.createQuery(
                            "SELECT c FROM Chapter c WHERE c.name = :name", Chapter.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(chapter);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Chapter> findByNameContainingIgnoreCase(String name) {
        return entityManager.createQuery(
                        "SELECT c FROM Chapter c WHERE LOWER(c.name) LIKE LOWER(:name)", Chapter.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public Chapter save(Chapter chapter) {
        if (chapter.getId() == null) {
            entityManager.persist(chapter);
            return chapter;
        } else {
            return entityManager.merge(chapter);
        }
    }

    public void deleteById(Long id) {
        Chapter chapter = entityManager.find(Chapter.class, id);
        if (chapter != null) {
            entityManager.remove(chapter);
        }
    }

    public boolean existsById(Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Chapter c WHERE c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Chapter c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public List<Chapter> findByMarinesCountGreaterThan(int minMarines) {
        return entityManager.createQuery(
                        "SELECT c FROM Chapter c WHERE SIZE(c.marines) > :minMarines", Chapter.class)
                .setParameter("minMarines", minMarines)
                .getResultList();
    }

    public List<Chapter> findByMarinesCountLessThan(int maxMarines) {
        return entityManager.createQuery(
                        "SELECT c FROM Chapter c WHERE SIZE(c.marines) < :maxMarines", Chapter.class)
                .setParameter("maxMarines", maxMarines)
                .getResultList();
    }
}