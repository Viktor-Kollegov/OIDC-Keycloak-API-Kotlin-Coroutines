CREATE TABLE oauth2_registered_client
(
    id                            VARCHAR(100) PRIMARY KEY,
    client_id                     VARCHAR(100) UNIQUE,
    client_id_issued_at           TIMESTAMP,
    client_secret                 VARCHAR(200),
    client_secret_expires_at      TIMESTAMP,
    client_name                   VARCHAR(200),
    client_authentication_methods VARCHAR(1000),
    authorization_grant_types     VARCHAR(1000),
    redirect_uris                 VARCHAR(1000),
    post_logout_redirect_uris     VARCHAR(1000),
    scopes                        VARCHAR(1000),
    client_settings               VARCHAR(2000),
    token_settings                VARCHAR(2000)
);

CREATE TABLE oauth2_authorization
(
    id                            VARCHAR(100) PRIMARY KEY,
    registered_client_id          VARCHAR(100),
    principal_name                VARCHAR(200),
    authorization_grant_type      VARCHAR(100),
    attributes                    CLOB,
    state                         VARCHAR(500),
    authorization_code_value      CLOB,
    authorization_code_issued_at  TIMESTAMP,
    authorization_code_expires_at TIMESTAMP,
    authorization_code_metadata   CLOB,
    access_token_value            CLOB,
    access_token_issued_at        TIMESTAMP,
    access_token_expires_at       TIMESTAMP,
    access_token_metadata         CLOB,
    access_token_type             VARCHAR(100),
    access_token_scopes           VARCHAR(1000),
    refresh_token_value           CLOB,
    refresh_token_issued_at       TIMESTAMP,
    refresh_token_expires_at      TIMESTAMP,
    refresh_token_metadata        CLOB,
    oidc_id_token_value           CLOB,
    oidc_id_token_issued_at       TIMESTAMP,
    oidc_id_token_expires_at      TIMESTAMP,
    oidc_id_token_metadata        CLOB
);