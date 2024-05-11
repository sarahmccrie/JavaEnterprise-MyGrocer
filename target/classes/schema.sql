/* 	Author:      Sarah McCrie 991405606
    Program:      A3_mccries
    Date:         [11-23-2023]
    Version:      1.0     
    Description:  This is my assignment 3*/

-- Role Table
CREATE TABLE sec_role (
  roleId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
  roleName VARCHAR(30) NOT NULL UNIQUE 
);

-- User Table
CREATE TABLE sec_user (
  userId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
  email VARCHAR(75) NOT NULL UNIQUE, 
  encryptedPassword VARCHAR(128) NOT NULL, 
  enabled BIT NOT NULL  
);

-- User Role Junction Table
CREATE TABLE user_role (
  userId BIGINT NOT NULL, 
  roleId BIGINT NOT NULL,
  FOREIGN KEY (userId) REFERENCES sec_user (userId), 
  FOREIGN KEY (roleId) REFERENCES sec_role (roleId),
  PRIMARY KEY(userId, roleId)
);

-- Customer Table
CREATE TABLE customer (
  customerId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
  firstName VARCHAR(45),
  lastName VARCHAR(45),
  userId BIGINT NOT NULL,
  FOREIGN KEY (userId) REFERENCES sec_user (userId)
);

-- Order Table
CREATE TABLE `order` (
  orderId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
  orderDate DATETIME NOT NULL,
  orderStatus VARCHAR(45) NOT NULL,
  customerId BIGINT NOT NULL,
  orderAmount DOUBLE,
  FOREIGN KEY (customerId) REFERENCES customer (customerId)
);

-- Address Table
CREATE TABLE address (
  addressId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  streetName VARCHAR(45) NOT NULL,
  streetNumber VARCHAR(45),
  city VARCHAR(45) NOT NULL,
  province VARCHAR(45) NOT NULL,
  postalCode VARCHAR(45) NOT NULL
);

-- Customer Address Association Table
CREATE TABLE customer_address (
  userId BIGINT NOT NULL,
  customerId BIGINT NOT NULL,
  addressId BIGINT NOT NULL,
  FOREIGN KEY (userId) REFERENCES sec_user (userId),
  FOREIGN KEY (customerId) REFERENCES customer (customerId),
  FOREIGN KEY (addressId) REFERENCES address (addressId)
);

-- Product Table
CREATE TABLE product (
  productId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  productName VARCHAR(100) NOT NULL,
  productPrice DOUBLE NOT NULL
);

-- Order Product Association Table
CREATE TABLE order_product (
  orderId BIGINT NOT NULL,
  productId BIGINT NOT NULL,
  quantity INT NOT NULL,
  FOREIGN KEY (orderId) REFERENCES `order` (orderId),
  FOREIGN KEY (productId) REFERENCES product (productId),
  PRIMARY KEY (orderId, productId)
);
