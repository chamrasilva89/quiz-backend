use sasip_db

INSERT INTO sasip_db.al_years (year, is_current, status, created_at, updated_at)
VALUES (2025, 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO `badge` (`name`, `description`, `icon_url`) VALUES
('Explorer', 'Awarded after completing the first quiz', 'path_to_explorer_icon'),
('Top Scorer', 'Awarded when the user wins a quiz', 'path_to_top_scorer_icon'),
('Speedster Pro', 'Awarded for finishing a quiz within half the time with all correct answers', 'path_to_speedster_pro_icon'),
('Half Centurion', 'Awarded after completing 50 quizzes', 'path_to_half_centurion_icon'),
('Streak Starter', 'Awarded after completing a 7-day streak', 'path_to_streak_starter_icon'),
('Streak King', 'Awarded after completing a 30-day streak', 'path_to_streak_king_icon');



INSERT INTO sasip_db.difficulty_point_mapping
(`level`, points)
VALUES('EASY', 10);
INSERT INTO sasip_db.difficulty_point_mapping
(`level`, points)
VALUES('HARD', 20);
INSERT INTO sasip_db.difficulty_point_mapping
(`level`, points)
VALUES('MEDIUM', 15);


INSERT INTO sasip_db.districts (id, code, name, province)
VALUES
(1, 'COL', 'Colombo', 'Western'),
(2, 'GAM', 'Gampaha', 'Western'),
(3, 'KLT', 'Kalutara', 'Western'),
(4, 'KDY', 'Kandy', 'Central'),
(5, 'MTL', 'Matale', 'Central'),
(6, 'NWL', 'Nuwara Eliya', 'Central'),
(7, 'GAL', 'Galle', 'Southern'),
(8, 'MAT', 'Matara', 'Southern'),
(9, 'HMB', 'Hambantota', 'Southern'),
(10, 'JAF', 'Jaffna', 'Northern'),
(11, 'KIL', 'Kilinochchi', 'Northern'),
(12, 'MAN', 'Mannar', 'Northern'),
(13, 'VAV', 'Vavuniya', 'Northern'),
(14, 'MUL', 'Mullaitivu', 'Northern'),
(15, 'BTC', 'Batticaloa', 'Eastern'),
(16, 'AMP', 'Ampara', 'Eastern'),
(17, 'TRI', 'Trincomalee', 'Eastern'),
(18, 'KUR', 'Kurunegala', 'North Western'),
(19, 'PUT', 'Puttalam', 'North Western'),
(20, 'ANA', 'Anuradhapura', 'North Central'),
(21, 'POL', 'Polonnaruwa', 'North Central'),
(22, 'BAD', 'Badulla', 'Uva'),
(23, 'MON', 'Monaragala', 'Uva'),
(24, 'RAT', 'Ratnapura', 'Sabaragamuwa'),
(25, 'KEG', 'Kegalle', 'Sabaragamuwa'),
(26, 'string', 'string', 'string');

INSERT INTO sasip_db.modules (module_id, name, description, created_date)
VALUES
(1, 'Classical Mechanics', 'Fundamental principles governing the motion of objects and forces acting upon them.', '2025-06-08 17:24:46'),
(2, 'Mechanics and Properties of Matter', 'At the end of the course, the students will be able to demonstrate (i) basic understanding on fundamental concepts of physics in mechanics and properties of matter and (ii) skills in relevant applications and solving problems.', '2025-06-08 18:36:39'),
(3, 'Electric Circuit Fundamentals', 'At the end of the course, the students will be able to analyze AC and DC circuits and explain their steady states with relevant calculations', '2025-08-09 13:44:14'),
(4, 'Elementary Physics Laboratory', 'Basic measuring instruments and measuring techniques, Venire concept, Uncertainties and errors of observations, Data acquisition, Analysis and presentation. ', '2025-08-09 13:45:16'),
(5, 'Waves and Optics', 'At the end of the course, the student will be able show (i) basic understanding on the fundamental concepts of vibrations and waves, optical physics and their applications and (ii) skills in applications and solving problems.', '2025-08-09 13:45:36');


ALTER TABLE modules
ADD COLUMN created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;



INSERT INTO sasip_db.roles (role_id, role_name)
VALUES
(2, 'STAFF'),
(1, 'STUDENT'),
(3, 'SUPER_ADMIN'),
(4, 'TEACHER');


INSERT INTO sasip_db.reward_gift (id, name, description, gift_type, created_at, updated_at, points)
VALUES
(1, 'free card', 'you will get a free card for a month', 'freecard', '2025-07-15 15:45:45', '2025-07-15 15:45:45', 0),
(2, 'free video', 'you will get a free video pack', 'freevidoes', '2025-07-15 15:46:39', '2025-07-15 15:46:39', 0),
(3, 'points', 'you will get some points', 'points', '2025-07-15 16:11:46', '2025-08-18 12:57:36', 100),
(4, 'points', 'you will get some points', 'Points', '2025-07-15 16:14:19', '2025-08-18 14:14:56', 200),
(5, 'Points', 'you will get some points', 'Points', '2025-07-15 16:14:51', '2025-08-18 14:15:16', 500);