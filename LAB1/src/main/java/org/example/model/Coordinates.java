package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "coordinates", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"x", "y"})
})
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coords_seq")
    @SequenceGenerator(name = "coords_seq", sequenceName = "coordinates_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "x", nullable = false)
    private Long x;

    @Column(name = "y", nullable = false)
    private long y;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }
}