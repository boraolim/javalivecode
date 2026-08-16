SET SCHEMA liverlivecode;

INSERT INTO liverlivecode.products (id_product, name_product, description_product, price_product)
VALUES ('EXT-001', 'PlayStation 5 Slim', 'Next-gen gaming console from Sony', 499.99),
       ('EXT-002', 'Nintendo Switch OLED', 'Portable family console', 349.99),
       ('EXT-003', 'Xbox Series X', 'Powerful console with GamePass', 499.99);

INSERT INTO liverlivecode.inventory (id_product, stock_product)
VALUES ('EXT-001', 100),
       ('EXT-002', 50),
       ('EXT-003', 200);