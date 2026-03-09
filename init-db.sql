-- Initialize Task Manager Database
-- This script is executed when MySQL container starts for the first time

-- Create users table (if not exists, as it's created by JPA)
-- This script is for additional initialization if needed

-- Insert default admin user (password: admin123 - BCrypt encoded)
-- Note: In production, use proper password hashing
INSERT INTO users (name, email, password, role, created_date, updated_date)
VALUES ('Admin', 'admin@taskmanager.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW4WzOFbMB3dHI.Hu', 'ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE email = email;

-- Insert sample user
INSERT INTO users (name, email, password, role, created_date, updated_date)
VALUES ('John Doe', 'john@taskmanager.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW4WzOFbMB3dHI.Hu', 'USER', NOW(), NOW())
ON DUPLICATE KEY UPDATE email = email;

-- Log initialization
SELECT 'Database initialized successfully' AS status;

