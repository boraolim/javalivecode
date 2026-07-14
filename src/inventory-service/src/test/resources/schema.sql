CREATE SCHEMA IF NOT EXISTS LIVERLIVECODE;

CREATE TABLE LIVERLIVECODE.PRODUCTS
(
    id_product              VARCHAR(50)   NOT NULL PRIMARY KEY,
    name_product            VARCHAR(255)  NOT NULL,
    description_product     VARCHAR(255)  NOT NULL,
    price_product           DECIMAL(18,2) NOT NULL DEFAULT 0
);

CREATE TABLE LIVERLIVECODE.INVENTORY
(
    id_product              VARCHAR(255)  NOT NULL PRIMARY KEY,
    stock_product           INT           NULL DEFAULT 0
);

-- Fin.