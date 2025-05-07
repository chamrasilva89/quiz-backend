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
CREATE TABLE questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id VARCHAR(50), 
    al_year YEAR,
    question_text TEXT NOT NULL,
    options JSON NOT NULL, 
    correct_option_index INT NOT NULL,
    explanation TEXT,
    subject VARCHAR(100),
    type VARCHAR(50),        
    subtype VARCHAR(100),    
    points INT DEFAULT 1,     
    difficulty_level VARCHAR(50), 
    max_time_sec INT DEFAULT 30, 
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT INTO questions (
    quiz_id, al_year, question_text, options, correct_option_index, explanation,
    subject, type, subtype, points, difficulty_level, max_time_sec
) VALUES (
    'quiz_001',
    2024,
    "What is Newton's second law?",
    '["F = ma", "E = mc^2", "V = IR", "P = mv"]',
    0,
    "F = ma defines the relationship between force, mass, and acceleration in Newton's second law.",
    "Physics",
    "MCQ",
    NULL,
    1,
    "Medium",
    30
);

CREATE TABLE quizzes (
    quiz_id VARCHAR(50) PRIMARY KEY,
    quiz_name VARCHAR(255) NOT NULL,
    intro TEXT,
    modules JSON,              -- Store as JSON array: ["Forces", "Motion"]
    time_limit INT,           
    xp INT,
    pass_accuracy INT,        
    al_year YEAR,
    attempts_allowed INT,
    scheduled_time DATETIME,
    deadline DATETIME,
    reward_ids JSON,          
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE quiz_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id VARCHAR(50),
    question_id INT,
    sort_order INT DEFAULT 1, 
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);
