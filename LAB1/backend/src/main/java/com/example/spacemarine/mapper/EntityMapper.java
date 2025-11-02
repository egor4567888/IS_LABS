package com.example.spacemarine.mapper;

import com.example.spacemarine.dto.*;
import com.example.spacemarine.entity.*;

public class EntityMapper {

    public static ChapterDto toDto(Chapter c) {
        if (c == null) return null;
        return new ChapterDto(c.getId(), c.getName(), c.getMarinesCount());
    }

    public static Chapter fromDto(ChapterDto dto) {
        if (dto == null) return null;
        Chapter c = new Chapter();
        c.setId(dto.getId());
        c.setName(dto.getName());
        return c;
    }

    public static SpaceMarineDto toDto(SpaceMarine sm) {
        if (sm == null) return null;
        SpaceMarineDto dto = new SpaceMarineDto();
        dto.setId(sm.getId());
        dto.setName(sm.getName());
        dto.setHealth(sm.getHealth());
        dto.setAchievements(sm.getAchievements());
        dto.setHeight(sm.getHeight());
        dto.setWeaponType(sm.getWeaponType());
        dto.setCreationDate(sm.getCreationDate());
        dto.setChapterId(sm.getChapter() != null ? sm.getChapter().getId() : null);
        if (sm.getCoordinates() != null)
            dto.setCoordinates(new CoordinatesDto(sm.getCoordinates().getX(), sm.getCoordinates().getY()));
        return dto;
    }

    public static SpaceMarine fromDto(SpaceMarineDto dto, Chapter chapter) {
        if (dto == null) return null;
        SpaceMarine sm = new SpaceMarine();
        sm.setName(dto.getName());
        sm.setHealth(dto.getHealth());
        sm.setAchievements(dto.getAchievements());
        sm.setHeight(dto.getHeight());
        sm.setWeaponType(dto.getWeaponType());
        sm.setChapter(chapter);
        if (dto.getCoordinates() != null) {
            Coordinates coords = new Coordinates();
            coords.setX(dto.getCoordinates().getX());
            coords.setY(dto.getCoordinates().getY());
            sm.setCoordinates(coords);
        }
        return sm;
    }
}
