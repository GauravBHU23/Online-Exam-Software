-- CBT Exam System - Database Schema (MySQL 8+)

CREATE DATABASE IF NOT EXISTS cbt_exam CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cbt_exam;

-- ---------------------------------------------------------------------
-- Candidates
-- ---------------------------------------------------------------------
CREATE TABLE candidate (
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

-- ---------------------------------------------------------------------
-- Exam (defines the server-controlled schedule/duration)
-- ---------------------------------------------------------------------
CREATE TABLE exam (
    exam_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_name        VARCHAR(150) NOT NULL,
    start_time       DATETIME     NOT NULL,
    duration_minutes INT          NOT NULL,
    total_questions  INT          NOT NULL,
    is_active        BOOLEAN      DEFAULT TRUE,
    has_sections     BOOLEAN      DEFAULT FALSE
);

-- ---------------------------------------------------------------------
-- Questions
-- ---------------------------------------------------------------------
CREATE TABLE question (
    question_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id          BIGINT       NOT NULL,
    question_text    TEXT         NOT NULL,
    option_a         VARCHAR(500) NOT NULL,
    option_b         VARCHAR(500) NOT NULL,
    option_c         VARCHAR(500) NOT NULL,
    option_d         VARCHAR(500) NOT NULL,
    correct_option   CHAR(1)      NOT NULL,   -- 'A' | 'B' | 'C' | 'D'
    question_order   INT          NOT NULL,
    marks            DECIMAL(5,2) DEFAULT 1.00,
    negative_marks   DECIMAL(5,2) DEFAULT 0.00,
    section_name     VARCHAR(50)  DEFAULT NULL,
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);

-- ---------------------------------------------------------------------
-- Exam session (one row per candidate attempt; tracks server-side timer)
-- ---------------------------------------------------------------------
CREATE TABLE exam_session (
    session_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id     BIGINT    NOT NULL UNIQUE,
    exam_id          BIGINT    NOT NULL,
    login_time       DATETIME  NOT NULL,
    server_end_time  DATETIME  NOT NULL,       -- login_time + duration, fixed at login
    status           ENUM('IN_PROGRESS','SUBMITTED','AUTO_SUBMITTED') DEFAULT 'IN_PROGRESS',
    submitted_at     DATETIME  NULL,
    FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);

-- ---------------------------------------------------------------------
-- Answers (candidate responses, auto-saved as they click)
-- ---------------------------------------------------------------------
CREATE TABLE answer (
    answer_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       BIGINT    NOT NULL,
    question_id      BIGINT    NOT NULL,
    selected_option  CHAR(1)   NULL,            -- NULL = not answered
    is_marked_review BOOLEAN   DEFAULT FALSE,
    answered_at      DATETIME  NULL,
    change_count     INT       DEFAULT 0,       -- how many times the candidate changed this answer
    first_viewed_at  DATETIME  NULL,            -- when the candidate first opened this question
    time_spent_seconds INT     DEFAULT 0,       -- cumulative seconds spent on this question
    UNIQUE KEY uq_session_question (session_id, question_id),
    FOREIGN KEY (session_id) REFERENCES exam_session(session_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

-- ---------------------------------------------------------------------
-- Result (computed at submit/auto-submit time)
-- ---------------------------------------------------------------------
CREATE TABLE result (
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

-- ---------------------------------------------------------------------
-- Proctoring events (tab-switch / fullscreen-exit / window-blur during exam)
-- ---------------------------------------------------------------------
CREATE TABLE proctoring_event (
    event_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       BIGINT       NOT NULL,
    event_type       ENUM('TAB_SWITCH','FULLSCREEN_EXIT','WINDOW_BLUR') NOT NULL,
    occurred_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES exam_session(session_id)
);

CREATE INDEX idx_question_exam ON question(exam_id, question_order);
CREATE INDEX idx_answer_session ON answer(session_id);
CREATE INDEX idx_proctoring_session ON proctoring_event(session_id);
