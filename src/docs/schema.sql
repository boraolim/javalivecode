DROP SCHEMA IF EXISTS `LIVERLIVECODE`;

CREATE SCHEMA IF NOT EXISTS `LIVERLIVECODE` DEFAULT CHARACTER SET UTF8;

USE LIVERLIVECODE;

CREATE TABLE `LIVERLIVECODE`.`PRODUCTS`
(
    `id_product`              VARCHAR(50)   NOT NULL PRIMARY KEY,
    `name_product`            VARCHAR(255)  NOT NULL,
    `description_product`     VARCHAR(255)  NOT NULL,
    `price_product`           DECIMAL(18,2) NOT NULL DEFAULT 0.0,
    INDEX `idx_description_product` (`description_product`) -- Índice para acelerar búsquedas funcionales por cliente
);

CREATE TABLE `LIVERLIVECODE`.`INVENTORY`
(
    `id_product`              VARCHAR(255)  NOT NULL,
    `stock_product`           INT           NULL DEFAULT NULL,
    PRIMARY KEY (`id_product`)
);

-- Consulta de catálogos.
SELECT * FROM LIVERLIVECODE.INVENTORY;
SELECT * FROM LIVERLIVECODE.PRODUCTS;

-- Fin.