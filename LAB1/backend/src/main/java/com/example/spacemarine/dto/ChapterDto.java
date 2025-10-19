package com.example.spacemarine.dto;

public class ChapterDto {
    private Long id;
    private String name;
    private Long marinesCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMarinesCount() {
        return marinesCount;
    }

    public void setMarinesCount(Long marinesCount) {
        this.marinesCount = marinesCount;
    }

    public ChapterDto(Long id, String name, Long marinesCount) {
        this.id = id;
        this.name = name;
        this.marinesCount = marinesCount;
    }
}
