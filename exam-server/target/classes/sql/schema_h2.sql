-- CBT Exam System - H2 schema (local dev/testing only).
-- Mirrors database/schema.sql but adapted for H2 in MySQL-compatibility mode
-- (no CREATE DATABASE/USE, ENUM replaced with VARCHAR + CHECK).

CREATE TABLE IF NOT EXISTS candidate (
    candidate_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_no   VARCHAR(30)  NOT NULL UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    name             VARCHAR(120) NOT NULL,
    father_name      VARCHAR(120) NOT NULL,
    centre_code      VARCHAR(20)  NOT NULL,
    roll_no          VARCHAR(30),
    photo_path       VARCHAR(255),
    exam_id          BIGINT       NOT NULL,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exam (
    exam_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_name        VARCHAR(150) NOT NULL,
    start_time       DATETIME     NOT NULL,
    duration_minutes INT          NOT NULL,
    total_questions  INT          NOT NULL,
    is_active        BOOLEAN      DEFAULT TRUE,
    has_sections     BOOLEAN      DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS question (
    question_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id          BIGINT       NOT NULL,
    question_text    TEXT         NOT NULL,
    option_a         VARCHAR(500) NOT NULL,
    option_b         VARCHAR(500) NOT NULL,
    option_c         VARCHAR(500) NOT NULL,
    option_d         VARCHAR(500) NOT NULL,
    correct_option   CHAR(1)      NOT NULL,
    question_order   INT          NOT NULL,
    marks            DECIMAL(5,2) DEFAULT 1.00,
    negative_marks   DECIMAL(5,2) DEFAULT 0.00,
    section_name     VARCHAR(50)  DEFAULT NULL,
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);

CREATE TABLE IF NOT EXISTS exam_session (
    session_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id     BIGINT    NOT NULL UNIQUE,
    exam_id          BIGINT    NOT NULL,
    login_time       DATETIME  NOT NULL,
    server_end_time  DATETIME  NOT NULL,
    status           VARCHAR(20) DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS','SUBMITTED','AUTO_SUBMITTED')),
    submitted_at     DATETIME  NULL,
    FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);

CREATE TABLE IF NOT EXISTS answer (
    answer_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       BIGINT    NOT NULL,
    question_id      BIGINT    NOT NULL,
    selected_option  CHAR(1)   NULL,
    is_marked_review BOOLEAN   DEFAULT FALSE,
    answered_at      DATETIME  NULL,
    change_count     INT       DEFAULT 0,
    first_viewed_at  DATETIME  NULL,
    time_spent_seconds INT     DEFAULT 0,
    UNIQUE (session_id, question_id),
    FOREIGN KEY (session_id) REFERENCES exam_session(session_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

CREATE TABLE IF NOT EXISTS result (
    result_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       BIGINT    NOT NULL UNIQUE,
    candidate_id     BIGINT    NOT NULL,
    total_marks      DECIMAL(7,2) NOT NULL,
    correct_count    INT       NOT NULL,
    wrong_count      INT       NOT NULL,
    unanswered_count INT       NOT NULL,
    generated_at     DATETIME  DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES exam_session(session_id),
    FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id)
);

CREATE TABLE IF NOT EXISTS proctoring_event (
    event_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       BIGINT       NOT NULL,
    event_type       VARCHAR(30)  NOT NULL CHECK (event_type IN ('TAB_SWITCH','FULLSCREEN_EXIT','WINDOW_BLUR')),
    occurred_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES exam_session(session_id)
);

CREATE INDEX IF NOT EXISTS idx_question_exam ON question(exam_id, question_order);
CREATE INDEX IF NOT EXISTS idx_answer_session ON answer(session_id);
CREATE INDEX IF NOT EXISTS idx_proctoring_session ON proctoring_event(session_id);
