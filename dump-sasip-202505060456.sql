CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(50),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url TEXT,
    school VARCHAR(150),
    al_year YEAR,
    district VARCHAR(100),
    medium VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(150) UNIQUE,
    username VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255),
    earned_xp INT DEFAULT 0,
    streak_count INT DEFAULT 0,
    average_score DECIMAL(5,2),
    total_quizzes_taken INT DEFAULT 0,
    parent_name VARCHAR(100),
    parent_contact_no VARCHAR(20),
    created_date DATETIME,
    updated_date DATETIME
);

CREATE TABLE performance_chart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    date DATE,
    accuracy INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE achievements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    badge_name VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE user_settings (
    user_id INT PRIMARY KEY,
    notifications_enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE question (
    question_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_text VARCHAR(1000) NOT NULL,
    options JSON NOT NULL,
    explanation TEXT,
    subject VARCHAR(255),
    type VARCHAR(255),
    sub_type VARCHAR(255),
    points INT,
    difficulty_level VARCHAR(255),
    max_time_sec INT,
    has_attachment BOOLEAN,
    module VARCHAR(255),
    submodule VARCHAR(255),
    correct_answer_id INT NOT NULL,
    al_year VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE question_attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    attachment_path VARCHAR(1000),
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE
);

CREATE TABLE quiz (
    quiz_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_name VARCHAR(255),
    intro TEXT,
    modules JSON,
    reward_ids JSON,
    question_ids JSON,
    attempts_allowed INT,
    pass_accuracy INT,
    time_limit INT,
    xp INT,
    scheduled_time DATETIME,
    deadline DATETIME,
    al_year VARCHAR(255),
    quiz_type VARCHAR(255),
    user_id BIGINT
);



CREATE TABLE user_quiz_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    quiz_id VARCHAR(100) NOT NULL,
    question_id BIGINT NOT NULL,
    submitted_answer_id BIGINT NOT NULL,
    correct_answer_id BIGINT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    awarded_points INT NOT NULL,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE question
ADD COLUMN status VARCHAR(1) DEFAULT 'A';

ALTER TABLE question
MODIFY COLUMN question_text TEXT;

ALTER TABLE question
MODIFY COLUMN explanation TEXT;
