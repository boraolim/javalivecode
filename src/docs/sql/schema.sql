DROP SCHEMA IF EXISTS `liverlivecode`;

CREATE SCHEMA IF NOT EXISTS `liverlivecode` DEFAULT CHARACTER SET UTF8;

USE liverlivecode;

CREATE TABLE `liverlivecode`.`products`
(
    `id_product`              VARCHAR(50)   NOT NULL PRIMARY KEY,
    `name_product`            VARCHAR(255)  NOT NULL,
    `description_product`     VARCHAR(255)  NOT NULL,
    `price_product`           DECIMAL(18,2) NOT NULL DEFAULT 0.0,
    INDEX `idx_name_product` (`name_product`),
    INDEX `idx_description_product` (`description_product`) -- Índice para acelerar búsquedas funcionales por cliente
);

CREATE TABLE `liverlivecode`.`inventory`
(
    `id_product`              VARCHAR(255)  NOT NULL,
    `stock_product`           INT           NULL DEFAULT NULL,
    PRIMARY KEY (`id_product`),
    CONSTRAINT fk_inventory_product FOREIGN KEY (id_product) REFERENCES products(id_product)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Consulta de catálogos.
SELECT * FROM liverlivecode.inventory;
SELECT * FROM liverlivecode.products;

-- Limpieza completa de tablas.
-- TRUNCATE TABLE liverlivecode.products;
-- TRUNCATE TABLE liverlivecode.inventory;

-- Fin.