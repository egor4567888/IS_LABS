package com.example.spacemarine.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Coordinates {

    @NotNull(message = "Coordinate x must not be null")
    @Max(value = 968, message = "Coordinate x must be <= 968")
    private Long x;

    private long y;

    public Coordinates() {}

    public Coordinates(Long x, long y) {
        this.x = x;
        this.y = y;
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
