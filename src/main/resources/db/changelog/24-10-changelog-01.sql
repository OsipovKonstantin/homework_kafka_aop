-- liquibase formatted sql

-- changeset osipov_ko:add_account
CREATE TABLE IF NOT EXISTS account
(
    id        BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    client_id BIGINT REFERENCES client(id),
    type      VARCHAR,
    balance     DECIMAL(19, 2),
    is_blocked BOOLEAN
);

-- changeset osipov_ko:add_transaction
CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    amount DECIMAL(19, 2),
    client_id BIGINT REFERENCES client(id),
    account_id BIGINT REFERENCES account(id),
    is_retry BOOLEAN,
    type VARCHAR(20)
);