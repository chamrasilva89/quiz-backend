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

CREATE TABLE modules (
    module_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE submodules (
    submodule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    module_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (module_id) REFERENCES modules(module_id) ON DELETE CASCADE
);

CREATE TABLE districts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(10) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  province VARCHAR(100) NOT NULL
);

INSERT INTO districts (code, name, province) VALUES 
('COL', 'Colombo', 'Western'),
('GAM', 'Gampaha', 'Western'),
('KLT', 'Kalutara', 'Western'),
('KDY', 'Kandy', 'Central'),
('MTL', 'Matale', 'Central'),
('NWL', 'Nuwara Eliya', 'Central'),
('GAL', 'Galle', 'Southern'),
('MAT', 'Matara', 'Southern'),
('HMB', 'Hambantota', 'Southern'),
('JAF', 'Jaffna', 'Northern'),
('KIL', 'Kilinochchi', 'Northern'),
('MAN', 'Mannar', 'Northern'),
('VAV', 'Vavuniya', 'Northern'),
('MUL', 'Mullaitivu', 'Northern'),
('BTC', 'Batticaloa', 'Eastern'),
('AMP', 'Ampara', 'Eastern'),
('TRI', 'Trincomalee', 'Eastern'),
('KUR', 'Kurunegala', 'North Western'),
('PUT', 'Puttalam', 'North Western'),
('ANA', 'Anuradhapura', 'North Central'),
('POL', 'Polonnaruwa', 'North Central'),
('BAD', 'Badulla', 'Uva'),
('MON', 'Monaragala', 'Uva'),
('RAT', 'Ratnapura', 'Sabaragamuwa'),
('KEG', 'Kegalle', 'Sabaragamuwa');

CREATE TABLE user_avatars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(512) NOT NULL,
    title VARCHAR(100),
    gender VARCHAR(10),         -- e.g., 'male', 'female', 'neutral' (optional)
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


ALTER TABLE quiz ADD COLUMN quiz_status VARCHAR(50) DEFAULT 'DRAFT';
