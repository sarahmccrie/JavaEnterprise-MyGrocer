/* 	Author:      Sarah McCrie 991405606
    Program:      A3_mccries
    Date:         [11-23-2023]
    Version:      1.0     
    Description:  This is my assignment 3*/

INSERT INTO `sec_user` (email, encryptedPassword, enabled)
VALUES ('mccries@sheridancollege.ca', '$2a$10$vzq5ZOJIlVYK0x.Ww9C7zeYkWSIAAnuVEqAgr1wij5.WnFdtZ3CiW', 1);

INSERT INTO `sec_user` (email, encryptedPassword, enabled)
VALUES ('john@sheridancollege.ca', '$2a$10$vzq5ZOJIlVYK0x.Ww9C7zeYkWSIAAnuVEqAgr1wij5.WnFdtZ3CiW', 1);

INSERT INTO `sec_user` (email, encryptedPassword, enabled)
VALUES ('eric@gmail.com', '$2a$10$vzq5ZOJIlVYK0x.Ww9C7zeYkWSIAAnuVEqAgr1wij5.WnFdtZ3CiW', 1);

INSERT INTO `sec_user` (email, encryptedPassword, enabled)
VALUES ('georgia@live.ca', '$2a$10$vzq5ZOJIlVYK0x.Ww9C7zeYkWSIAAnuVEqAgr1wij5.WnFdtZ3CiW', 1);

 
INSERT INTO `sec_role` (roleName)
VALUES ('ROLE_USER');
 
INSERT INTO `sec_role` (roleName)
VALUES ('ROLE_GUEST');

INSERT INTO `sec_role` (roleName)
VALUES ('ROLE_ADMIN');
 
 
INSERT INTO `user_role` (userId, roleId)
VALUES (1, 3);
 
INSERT INTO `user_role` (userId, roleId)
VALUES (2, 2);

INSERT INTO `user_role` (userId, roleId)
VALUES (3, 1);

INSERT INTO `user_role` (userId, roleId)
VALUES (4, 1);


INSERT INTO `customer` (firstName, lastName, userId)
VALUES ('Sarah', 'McCrie', 1);

INSERT INTO `customer` (firstName, lastName, userId)
VALUES ('John', 'Smith', 2);

INSERT INTO `customer` (firstName, lastName, userId)
VALUES ('Eric', 'Johnstone', 3);

INSERT INTO `customer` (firstName, lastName, userId)
VALUES ('Georgia', 'Peach', 4);


INSERT INTO address (streetName, streetNumber, city, province, postalCode) VALUES ('Maple Street', '123', 'Oakville', 'Ontario', 'L3J5U7');
INSERT INTO address (streetName, streetNumber, city, province, postalCode) VALUES ('Oak Avenue', '456', 'Mississauga', 'Ontario', 'L4K8D2');
INSERT INTO address (streetName, streetNumber, city, province, postalCode) VALUES ('Pine Lane', '789', 'Burlington', 'Ontario', 'L2Y4B9');
INSERT INTO address (streetName, streetNumber, city, province, postalCode) VALUES ('Elm Road', '101', 'Toronto', 'Ontario', 'M2HS7V');


INSERT INTO customer_address (userId, customerId, addressId) VALUES (1, 1, 1);
INSERT INTO customer_address (userId, customerId, addressId) VALUES (2, 2, 3);
INSERT INTO customer_address (userId, customerId, addressId) VALUES (3, 3, 2);
INSERT INTO customer_address (userId, customerId, addressId) VALUES (4, 4, 4);


INSERT INTO `order` (orderDate, orderStatus, customerId, orderAmount) VALUES
('2023-11-21 05:57:32.858947', 'Processing', 1, 129.40),
('2023-11-15 12:07:09.928247', 'Shipped', 2, 302.14),
('2023-01-21 08:37:38.828247', 'Delivered', 3, 96.61),
('2023-06-24 15:37:38.828247', 'Cancelled', 4, 492.18);


INSERT INTO product (productName, productPrice) VALUES
('Cheddar Cheese', 8.49),
('Milk', 3.29),
('Apple', 4.59),
('Ground Beef', 21.99);