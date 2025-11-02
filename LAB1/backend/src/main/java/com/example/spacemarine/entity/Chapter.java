package com.example.spacemarine.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Chapter name must not be null")
    @NotBlank(message = "Chapter name must not be blank")
    private String name;

    @NotNull(message = "marinesCount must not be null")
    @Min(value = 1, message = "marinesCount must be > 0")
    @Max(value = 1000, message = "marinesCount must be <= 1000")
    @Column(name = "marines_count")
    private Long marinesCount;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<SpaceMarine> marines = new HashSet<>();

    public Chapter() {}

    public Chapter(String name, Long marinesCount) {
        this.name = name;
        this.marinesCount = marinesCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Transient
    public Long getMarinesCount() {
        return marines != null ? (long) marines.size() : 0L;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Set<SpaceMarine> getMarines() {
        return marines;
    }

    public void setMarines(Set<SpaceMarine> marines) {
        this.marines = marines;
    }
}
