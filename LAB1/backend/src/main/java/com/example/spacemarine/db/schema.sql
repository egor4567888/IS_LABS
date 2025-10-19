-- chapters table
CREATE TABLE IF NOT EXISTS chapters (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  marines_count BIGINT NOT NULL
);

-- space_marines table
CREATE TABLE IF NOT EXISTS space_marines (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  coord_x BIGINT NOT NULL,
  coord_y BIGINT NOT NULL,
  creation_date TIMESTAMP NOT NULL,
  chapter_id BIGINT NOT NULL,
  health BIGINT NOT NULL,
  achievements VARCHAR(1000) NOT NULL,
  height DOUBLE,
  weapon_type VARCHAR(50),
  CONSTRAINT fk_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

-- view for grouping by achievements (used by special operation)
CREATE VIEW IF NOT EXISTS v_group_achievements AS
SELECT achievements, COUNT(*) AS cnt FROM space_marines GROUP BY achievements;
