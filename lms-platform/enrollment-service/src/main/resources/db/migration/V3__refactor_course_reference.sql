TRUNCATE TABLE course_metrics;

ALTER TABLE course_metrics RENAME TO course_references;

ALTER TABLE course_references
ALTER COLUMN total_lessons TYPE BIGINT;

ALTER TABLE course_references
    ADD COLUMN instructor_id VARCHAR(36) NOT NULL;