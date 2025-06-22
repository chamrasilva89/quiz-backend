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


CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (role_name) VALUES ('STUDENT'), ('STAFF'), ('SUPER_ADMIN');

CREATE TABLE permissions (
    permission_id INT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(100) UNIQUE NOT NULL
);

INSERT INTO permissions (permission_name) VALUES 
('LOGIN'),
('CHANGE_PASSWORD'),
('CREATE_QUESTION'),
('UPDATE_QUESTION'),
('CREATE_QUIZ'),
('UPDATE_QUIZ'),
('SUBMIT_SASIP_QUIZ'),
('VIEW_SCOREBOARD'),
('GENERATE_DYNAMIC_QUIZ');


CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

CREATE TABLE leaderboard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    username VARCHAR(100),
    school VARCHAR(150),
    district VARCHAR(100),
    al_year YEAR,
    total_points INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_leaderboard_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_leaderboard_al_year ON leaderboard(al_year);
CREATE INDEX idx_leaderboard_district_al_year ON leaderboard(district, al_year);
CREATE INDEX idx_leaderboard_school_al_year ON leaderboard(school, al_year);

CREATE TABLE user_quiz_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT     NOT NULL,
    quiz_id VARCHAR(50) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time   DATETIME NOT NULL,
    time_taken_seconds INT NOT NULL,
    total_questions   INT NOT NULL,
    correct_count     INT NOT NULL,
    wrong_count       INT NOT NULL,
    raw_score         INT NOT NULL,
    speed_bonus       DECIMAL(8,2) NOT NULL,
    total_score       DECIMAL(8,2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

ALTER TABLE user_quiz_submission
  MODIFY COLUMN end_time DATETIME     NULL,
  MODIFY COLUMN time_taken_seconds INT NULL,
  MODIFY COLUMN total_questions   INT NULL,
  MODIFY COLUMN correct_count     INT NULL,
  MODIFY COLUMN wrong_count       INT NULL,
  MODIFY COLUMN raw_score         INT NULL,
  MODIFY COLUMN speed_bonus       DECIMAL(8,2) NULL,
  MODIFY COLUMN total_score       DECIMAL(8,2) NULL;

ALTER TABLE users
ADD COLUMN user_status VARCHAR(20) NOT NULL DEFAULT 'active';


CREATE TABLE monthly_leaderboard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    username VARCHAR(255),
    school VARCHAR(255),
    district VARCHAR(255),
    al_year INT,
    month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    total_points INT DEFAULT 0,
    updated_at DATETIME,

    UNIQUE KEY uq_user_month (user_id, month)
);

CREATE TABLE badge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    points INT NOT NULL DEFAULT 0,
    icon_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(1000) NOT NULL,
    blacklisted_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL
);


ALTER TABLE reward
    ADD COLUMN max_quantity INT DEFAULT NULL,
    ADD COLUMN type VARCHAR(50) DEFAULT NULL,
    ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE',
    ADD COLUMN valid_from DATETIME DEFAULT NULL,
    ADD COLUMN valid_to DATETIME DEFAULT NULL,
    ADD COLUMN is_claimable BOOLEAN DEFAULT FALSE;

ALTER TABLE quiz
ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;


ALTER TABLE users MODIFY email VARCHAR(255) NULL;
