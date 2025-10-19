package com.example.spacemarine.mapper;

import com.example.spacemarine.dto.*;
import com.example.spacemarine.entity.*;

public class SpaceMarineMapper {

    public static SpaceMarineDto toDto(SpaceMarine e) {
        if (e == null) return null;
        SpaceMarineDto dto = new SpaceMarineDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setCreationDate(e.getCreationDate());
        dto.setHealth(e.getHealth());
        dto.setAchievements(e.getAchievements());
        dto.setHeight(e.getHeight());
        dto.setWeaponType(e.getWeaponType());
        dto.setChapterId(e.getChapter() != null ? e.getChapter().getId() : null);

        if (e.getCoordinates() != null) {
            CoordinatesDto c = new CoordinatesDto();
            c.setX(e.getCoordinates().getX());
            c.setY(e.getCoordinates().getY());
            dto.setCoordinates(c);
        }
        return dto;
    }

    public static SpaceMarine fromDto(SpaceMarineDto dto, Chapter ch) {
        if (dto == null) return null;
        SpaceMarine e = new SpaceMarine();
        e.setId(dto.getId());
        e.setName(dto.getName());
        e.setHealth(dto.getHealth());
        e.setAchievements(dto.getAchievements());
        e.setHeight(dto.getHeight());
        e.setWeaponType(dto.getWeaponType());
        e.setChapter(ch);
        if (dto.getCoordinates() != null) {
            Coordinates c = new Coordinates();
            c.setX(dto.getCoordinates().getX());
            c.setY(dto.getCoordinates().getY());
            e.setCoordinates(c);
        }
        return e;
    }
}
