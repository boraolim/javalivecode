SET SCHEMA liverlivecode;

INSERT INTO liverlivecode.products (id_product, name_product, description_product, price_product)
VALUES ('EXT-001', 'PlayStation 5 Slim', 'Next-gen gaming console from Sony', 499.99),
       ('EXT-002', 'Nintendo Switch OLED', 'Portable family console', 349.99),
       ('EXT-003', 'Xbox Series X', 'Powerful console with GamePass', 499.99),
       ('EXT-004', 'Laptop Gamer', 'Gaming laptop', 1000.00),
       ('EXT-005', 'Laptop for Office', 'Office laptop', 850.00),
       ('EXT-006', 'Domestic laptop', 'Home laptop', 950.00),
       ('EXT-008', 'Tablet Huawei MatePad 11.5', 'Tablet de 11.5 pulgadas con 8 GB de RAM y 128 GB de almacenamiento.', 7499.00);

INSERT INTO liverlivecode.inventory (id_product, stock_product)
VALUES ('EXT-001', 100),
       ('EXT-002', 50),
       ('EXT-003', 200),
       ('EXT-004', 100),
       ('EXT-005', 75),
       ('EXT-006', 0);