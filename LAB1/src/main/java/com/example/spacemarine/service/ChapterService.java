package com.example.spacemarine.service;

import com.example.spacemarine.entity.Chapter;
import com.example.spacemarine.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ChapterService {

    private final ChapterRepository repo;
    private final JdbcTemplate jdbc;

    @Autowired
    public ChapterService(ChapterRepository repo, JdbcTemplate jdbc) {
        this.repo = repo;
        this.jdbc = jdbc;
    }

    public List<Chapter> listAll() {
        return repo.findAll();
    }

    public Optional<Chapter> get(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Chapter create(Chapter c) {
        return repo.save(c);
    }

    @Transactional
    public Chapter update(Long id, Chapter c) {
        Chapter exist = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Chapter not found"));
        exist.setName(c.getName());
        exist.setMarinesCount(c.getMarinesCount());
        return repo.save(exist);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // create via DB function (Postgres) or fallback to repo
    public Long createChapterViaDb(String name, Long count) {
        try {
            // call function fn_create_chapter(nm, count) returning id
            Long newId = jdbc.queryForObject("SELECT fn_create_chapter(?, ?)", Long.class, name, count);
            return newId;
        } catch (Exception ex) {
            Chapter ch = new Chapter(name, count);
            Chapter saved = repo.save(ch);
            return saved.getId();
        }
    }

    public void dissolveChapterViaDb(Long id) {
        try {
            jdbc.update("SELECT fn_dissolve_chapter(?)", id);
        } catch (Exception ex) {
            repo.deleteById(id);
        }
    }
}
