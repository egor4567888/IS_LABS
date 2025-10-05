package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loc_seq")
    @SequenceGenerator(name = "loc_seq", sequenceName = "locations_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name = "y", nullable = false)
    private long y;

    @Column(name = "z", nullable = false)
    private int z;

    @NotNull
    @Size(max = 453)
    @Column(name = "name", nullable = false, length = 453)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}