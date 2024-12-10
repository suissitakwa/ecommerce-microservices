CREATE TABLE IF NOT EXISTS category (
    id          INTEGER NOT NULL PRIMARY KEY,
    description VARCHAR(255),
    name        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product (
    id                 INTEGER NOT NULL PRIMARY KEY,
    available_quantity DOUBLE PRECISION NOT NULL,
    description        VARCHAR(255),
    name               VARCHAR(255),
    price              NUMERIC(38, 2),
    category_id        INTEGER CONSTRAINT fk1mtsbur82frn64de7balymq9s REFERENCES category
);

create sequence if not exists category_seq increment by 50;
create sequence if not exists product_seq increment by 50;