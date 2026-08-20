CREATE TABLE user_profiles (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL CONSTRAINT uk_user_profiles_email UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    avatar_url VARCHAR(500),
    phone_number VARCHAR(20),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);