-- Idempotent migration for existing H2 dev databases created before sections/photo/attempt-tracking were added.
ALTER TABLE exam ADD COLUMN IF NOT EXISTS has_sections BOOLEAN DEFAULT FALSE;
ALTER TABLE question ADD COLUMN IF NOT EXISTS section_name VARCHAR(50) DEFAULT NULL;
ALTER TABLE answer ADD COLUMN IF NOT EXISTS change_count INT DEFAULT 0;
ALTER TABLE answer ADD COLUMN IF NOT EXISTS first_viewed_at DATETIME NULL;
ALTER TABLE answer ADD COLUMN IF NOT EXISTS time_spent_seconds INT DEFAULT 0;

CREATE TABLE IF NOT EXISTS proctoring_event (
    event_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       BIGINT       NOT NULL,
    event_type       VARCHAR(30)  NOT NULL CHECK (event_type IN ('TAB_SWITCH','FULLSCREEN_EXIT','WINDOW_BLUR')),
    occurred_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES exam_session(session_id)
);

CREATE INDEX IF NOT EXISTS idx_proctoring_session ON proctoring_event(session_id);
