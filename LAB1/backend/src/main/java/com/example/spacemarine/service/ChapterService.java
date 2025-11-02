package com.example.spacemarine.service;

import com.example.spacemarine.entity.Chapter;
import com.example.spacemarine.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

@Service
public class ChapterService {

    private final ChapterRepository repo;
    private final JdbcTemplate jdbc;
    private final SimpMessagingTemplate messaging;

    @Autowired
    public ChapterService(ChapterRepository repo, JdbcTemplate jdbc, SimpMessagingTemplate messaging) {
        this.repo = repo;
        this.jdbc = jdbc;
        this.messaging = messaging;
    }

    public List<Chapter> listAll() {
        return repo.findAll();
    }

    public Optional<Chapter> get(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Chapter create(Chapter c) {
        Chapter saved = repo.save(c);
        notifyClients("create", saved.getId());
        return saved;
    }

    @Transactional
    public Chapter update(Long id, Chapter c) {
        Chapter exist = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Chapter not found"));
        exist.setName(c.getName());
        Chapter saved = repo.save(exist);
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

    public Long createChapterViaDb(String name, Long count) {
        try {
            Long newId = jdbc.queryForObject("SELECT fn_create_chapter(?, ?)", Long.class, name, count);
            notifyClients("create", newId);
            return newId;
        } catch (Exception ex) {
            Chapter ch = new Chapter(name, count);
            Chapter saved = repo.save(ch);
            notifyClients("create", saved.getId());
            return saved.getId();
        }
    }

    public void dissolveChapterViaDb(Long id) {
        try {
            jdbc.update("SELECT fn_dissolve_chapter(?)", id);
            notifyClients("delete", id);
            notifyMarineClientsAboutChapterDeletion(id);
        } catch (Exception ex) {
            if (repo.existsById(id)) {
                repo.deleteById(id);
                notifyClients("delete", id);
                notifyMarineClientsAboutChapterDeletion(id);
            }
        }
    }

    private void notifyClients(String action, Long id) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("action", action);
                    payload.put("id", id);
                    payload.put("type", "chapter");
                    messaging.convertAndSend("/topic/chapters", payload);
                }
            });
        } else {
            Map<String, Object> payload = new HashMap<>();
            payload.put("action", action);
            payload.put("id", id);
            payload.put("type", "chapter");
            messaging.convertAndSend("/topic/chapters", payload);
        }
    }

    private void notifyMarineClientsAboutChapterDeletion(Long chapterId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "chapter_deleted");
        payload.put("chapterId", chapterId);
        payload.put("type", "marine_cleanup");
        messaging.convertAndSend("/topic/spaceMarines", payload);
    }
}
