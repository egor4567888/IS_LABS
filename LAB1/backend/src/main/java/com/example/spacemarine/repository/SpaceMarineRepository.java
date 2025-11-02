package com.example.spacemarine.repository;

import com.example.spacemarine.entity.SpaceMarine;
import com.example.spacemarine.entity.Weapon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class SpaceMarineRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<SpaceMarine> findById(Long id) {
        try {
            SpaceMarine marine = entityManager.find(SpaceMarine.class, id);
            return Optional.ofNullable(marine);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<SpaceMarine> findAll() {
        return entityManager.createQuery("FROM SpaceMarine", SpaceMarine.class)
                .getResultList();
    }

    public Page<SpaceMarine> findAll(Pageable pageable) {
        TypedQuery<SpaceMarine> query = entityManager.createQuery(
                "FROM SpaceMarine", SpaceMarine.class);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<SpaceMarine> content = query.getResultList();

        Long total = entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm", Long.class)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<SpaceMarine> findByName(String name, Pageable pageable) {
        TypedQuery<SpaceMarine> query = entityManager.createQuery(
                        "SELECT sm FROM SpaceMarine sm WHERE sm.name = :name", SpaceMarine.class)
                .setParameter("name", name);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<SpaceMarine> content = query.getResultList();

        Long total = entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<SpaceMarine> findByAchievements(String achievements, Pageable pageable) {
        TypedQuery<SpaceMarine> query = entityManager.createQuery(
                        "SELECT sm FROM SpaceMarine sm WHERE sm.achievements = :achievements", SpaceMarine.class)
                .setParameter("achievements", achievements);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<SpaceMarine> content = query.getResultList();

        Long total = entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.achievements = :achievements", Long.class)
                .setParameter("achievements", achievements)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public long countByWeaponType(Weapon weapon) {
        return entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.weaponType = :weapon", Long.class)
                .setParameter("weapon", weapon)
                .getSingleResult();
    }

    public long countByHealthGreaterThan(Long health) {
        return entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.health > :health", Long.class)
                .setParameter("health", health)
                .getSingleResult();
    }

    public long countByChapterId(Long chapterId) {
        return entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.chapter.id = :chapterId", Long.class)
                .setParameter("chapterId", chapterId)
                .getSingleResult();
    }

    public List<SpaceMarine> findByChapterId(Long chapterId) {
        return entityManager.createQuery(
                        "SELECT sm FROM SpaceMarine sm WHERE sm.chapter.id = :chapterId", SpaceMarine.class)
                .setParameter("chapterId", chapterId)
                .getResultList();
    }

    public SpaceMarine save(SpaceMarine marine) {
        if (marine.getId() == null) {
            entityManager.persist(marine);
            return marine;
        } else {
            return entityManager.merge(marine);
        }
    }

    public void deleteById(Long id) {
        SpaceMarine marine = entityManager.find(SpaceMarine.class, id);
        if (marine != null) {
            entityManager.remove(marine);
        }
    }

    public boolean existsById(Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }
}