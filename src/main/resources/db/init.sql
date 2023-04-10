DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS discount_card;

CREATE TABLE IF NOT EXISTS product (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)     NOT NULL,
    price          DOUBLE PRECISION NOT NULL,
    is_promotional BOOLEAN          NOT NULL
);

CREATE TABLE IF NOT EXISTS discount_card (
    id       BIGSERIAL PRIMARY KEY,
    number   INTEGER          NOT NULL UNIQUE,
    discount DOUBLE PRECISION NOT NULL
);