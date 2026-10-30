-- H2 seed data for local dev/testing.
-- Password for APP1001 is 'password123' (SHA-256 hashed, see com.cbtexam.util.PasswordUtil).

INSERT INTO exam (exam_name, start_time, duration_minutes, total_questions, is_active)
VALUES ('Sample Mock Test', NOW(), 2, 5, TRUE);

INSERT INTO candidate (application_no, password_hash, name, father_name, centre_code, roll_no, exam_id)
VALUES ('APP1001', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Test Candidate', 'Test Father', 'C001', 'R1001',
        (SELECT exam_id FROM exam WHERE exam_name = 'Sample Mock Test'));

INSERT INTO question (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, question_order)
SELECT exam_id, 'What is the capital of India?', 'Mumbai', 'New Delhi', 'Kolkata', 'Chennai', 'B', 1 FROM exam WHERE exam_name = 'Sample Mock Test';

INSERT INTO question (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, question_order)
SELECT exam_id, 'Which gas do plants absorb from the atmosphere?', 'Oxygen', 'Nitrogen', 'Carbon Dioxide', 'Hydrogen', 'C', 2 FROM exam WHERE exam_name = 'Sample Mock Test';

INSERT INTO question (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, question_order)
SELECT exam_id, '2 + 2 * 2 = ?', '8', '6', '4', '10', 'B', 3 FROM exam WHERE exam_name = 'Sample Mock Test';

INSERT INTO question (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, question_order)
SELECT exam_id, 'HTML stands for?', 'Hyper Text Markup Language', 'High Text Markup Language', 'Hyper Text Made Language', 'None', 'A', 4 FROM exam WHERE exam_name = 'Sample Mock Test';

INSERT INTO question (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, question_order)
SELECT exam_id, 'Which is the largest planet in the Solar System?', 'Earth', 'Mars', 'Jupiter', 'Venus', 'C', 5 FROM exam WHERE exam_name = 'Sample Mock Test';
