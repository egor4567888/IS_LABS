INSERT INTO chapters(name, marines_count) VALUES ('Ultramarines', 10);
INSERT INTO chapters(name, marines_count) VALUES ('Blood Angels', 5);
INSERT INTO chapters(name, marines_count) VALUES ('Space Wolves', 7);

INSERT INTO space_marines(name, coord_x, coord_y, creation_date, chapter_id, health, achievements, height, weapon_type)
VALUES ('Marneus', 100, 200, CURRENT_TIMESTAMP, 1, 100, 'Defender', 2.0, 'HEAVY_BOLTGUN');

INSERT INTO space_marines(name, coord_x, coord_y, creation_date, chapter_id, health, achievements, height, weapon_type)
VALUES ('Sanguinius', 50, -10, CURRENT_TIMESTAMP, 2, 120, 'Striker', 1.95, 'PLASMA_GUN');

INSERT INTO space_marines(name, coord_x, coord_y, creation_date, chapter_id, health, achievements, height, weapon_type)
VALUES ('Ragnar', 300, 400, CURRENT_TIMESTAMP, 3, 90, 'Scout', 1.8, 'BOLT_PISTOL');
