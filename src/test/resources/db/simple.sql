-- ### Customers #######################################################################################################

INSERT INTO customers(id, email, first_name, last_name, birthday, created, last_modified)
VALUES ('12345678-90ab-cdef-1234-567890ab0101', 'max.mustermann@gmail.com', 'Max', 'Mustermann', '1980-01-02',
        '2020-06-15T12:34:56Z', '2022-06-15T23:45:12Z'),
       ('12345678-90ab-cdef-1234-567890ab0102', 'maria.mustermeyer@web.de', 'Maria', 'Mustermeyer', '1986-03-04',
        '2020-07-15T12:34:56Z', '2022-07-15T23:45:12Z');

-- ### Products ########################################################################################################

INSERT INTO products(id, name, created, last_modified)
VALUES ('12345678-90ab-cdef-1234-567890ab0201', 'Product 1', '2020-01-01T12:34:56Z', '2022-02-02T23:45:12Z'),
       ('12345678-90ab-cdef-1234-567890ab0202', 'Product 2', '2020-02-02T12:34:56Z', '2022-03-03T23:45:12Z'),
       ('12345678-90ab-cdef-1234-567890ab0203', 'Product 3', '2020-02-03T12:34:56Z', '2022-04-03T23:45:12Z');

-- ### Orders ##########################################################################################################

INSERT INTO customer_orders(id, customer_id, product_id, quantity, state, created, last_modified)
VALUES ('12345678-90ab-cdef-1234-567890ab0301', '12345678-90ab-cdef-1234-567890ab0101',
        '12345678-90ab-cdef-1234-567890ab0201', 1, 'NEW', current_timestamp - 31, current_timestamp - 32),
       ('12345678-90ab-cdef-1234-567890ab0302', '12345678-90ab-cdef-1234-567890ab0101',
        '12345678-90ab-cdef-1234-567890ab0202', 1, 'ACCEPTED', current_timestamp - 28, current_timestamp - 29),
       ('12345678-90ab-cdef-1234-567890ab0303', '12345678-90ab-cdef-1234-567890ab0101',
        '12345678-90ab-cdef-1234-567890ab0202', 1, 'ACCEPTED', current_timestamp - 1, current_timestamp - 1),
       ('12345678-90ab-cdef-1234-567890ab0304', '12345678-90ab-cdef-1234-567890ab0102',
        '12345678-90ab-cdef-1234-567890ab0203', 1, 'SUCCESS', current_timestamp - 1, current_timestamp - 1);

