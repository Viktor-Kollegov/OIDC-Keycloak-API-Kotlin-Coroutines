CREATE TABLE users
(
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100),
    enabled  BOOLEAN
);

CREATE TABLE authorities
(
    username  VARCHAR(50),
    authority VARCHAR(50)
);

ALTER TABLE authorities
    ADD CONSTRAINT fk_user_authorities
        FOREIGN KEY (username) REFERENCES users (username);

ALTER TABLE authorities
    ADD CONSTRAINT uk_user_auth
        UNIQUE (username, authority);