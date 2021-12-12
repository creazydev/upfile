CREATE TABLE file (
    id VARCHAR(255) PRIMARY KEY NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    media_type VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL
);

