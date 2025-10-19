package com.example.spacemarine.dto;

public class CoordinatesDto {
    private Long x;
    private long y;

    public CoordinatesDto() {
    }

    public CoordinatesDto(Long x, long y) {
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
