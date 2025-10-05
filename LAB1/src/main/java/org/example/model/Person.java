package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    @SequenceGenerator(name = "persons_seq", sequenceName = "persons_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(min = 1)
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color hairColor;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Min(1)
    @Column(nullable = false)
    private float height;

    @NotNull
    @Column(nullable = false)
    private ZonedDateTime birthday;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country nationality;

    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date();
    }

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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public ZonedDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(ZonedDateTime birthday) {
        this.birthday = birthday;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }
}
