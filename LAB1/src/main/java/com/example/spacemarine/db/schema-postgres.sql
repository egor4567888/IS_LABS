CREATE TABLE chapters (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  marines_count BIGINT NOT NULL CHECK (marines_count > 0 AND marines_count <= 1000)
);

CREATE TABLE space_marines (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  coord_x BIGINT NOT NULL CHECK (coord_x <= 968),
  coord_y BIGINT NOT NULL,
  creation_date TIMESTAMP NOT NULL DEFAULT now(),
  chapter_id BIGINT NOT NULL REFERENCES chapters(id) ON DELETE CASCADE,
  health BIGINT NOT NULL CHECK (health > 0),
  achievements TEXT NOT NULL,
  height DOUBLE PRECISION,
  weapon_type VARCHAR(50) -- could be declared as ENUM in PG
);

-- Функция: сгруппировать по achievements
CREATE OR REPLACE FUNCTION fn_group_by_achievements()
RETURNS TABLE(achievements TEXT, cnt bigint) AS $$
BEGIN
  RETURN QUERY SELECT achievements, COUNT(*) FROM space_marines GROUP BY achievements;
END; $$ LANGUAGE plpgsql;

-- Функция: count weaponType less than given (ordinal)
CREATE OR REPLACE FUNCTION fn_count_weapon_less_than(target TEXT)
RETURNS bigint AS $$
DECLARE
  target_ord int;
BEGIN
  -- предполагаем порядок перечисления в Java: HEAVY_BOLTGUN(0), BOLT_PISTOL(1), ...
  IF target IS NULL THEN RETURN 0; END IF;
  SELECT ordinal INTO target_ord FROM (VALUES
    ('HEAVY_BOLTGUN',0),('BOLT_PISTOL',1),('PLASMA_GUN',2),('HEAVY_FLAMER',3)
  ) AS t(name, ordinal) WHERE name = target;

  RETURN (SELECT COUNT(*) FROM space_marines sm
          WHERE sm.weapon_type IS NOT NULL
            AND (CASE sm.weapon_type
                WHEN 'HEAVY_BOLTGUN' THEN 0
                WHEN 'BOLT_PISTOL' THEN 1
                WHEN 'PLASMA_GUN' THEN 2
                WHEN 'HEAVY_FLAMER' THEN 3
                ELSE 999 END) < target_ord);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_count_health_greater_than(threshold bigint)
RETURNS bigint AS $$
BEGIN
  RETURN (SELECT COUNT(*) FROM space_marines WHERE health > threshold);
END;
$$ LANGUAGE plpgsql;

-- Создать орден:
CREATE OR REPLACE FUNCTION fn_create_chapter(nm TEXT, count bigint)
RETURNS bigint AS $$
DECLARE new_id bigint;
BEGIN
  INSERT INTO chapters(name, marines_count) VALUES (nm, count) RETURNING id INTO new_id;
  RETURN new_id;
END;
$$ LANGUAGE plpgsql;

-- Распустить орден (удаление chapter и всех marines через cascade)
CREATE OR REPLACE FUNCTION fn_dissolve_chapter(ch_id bigint)
RETURNS void AS $$
BEGIN
  DELETE FROM chapters WHERE id = ch_id;
END;
$$ LANGUAGE plpgsql;
