CREATE TABLE book
(
    id CHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL,
    publish_date DATE NOT NULL
)