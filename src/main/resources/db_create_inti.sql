CREATE TABLE `categories` (
  `cat_id` int(11) NOT NULL,
  `cat_name` varchar(100) NOT NULL,
  `cat_description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`cat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `products` (
  `prod_id` int(11) NOT NULL,
  `prod_name` varchar(60) NOT NULL,
  `prod_short_desc` varchar(200) DEFAULT NULL,
  `prod_description` varchar(500) DEFAULT NULL,
  `prod_price` double(5,2) DEFAULT '0.00',
  `prod_sale_price` double(5,2) DEFAULT '0.00',
  `prod_unit` varchar(50) DEFAULT 'stuk',
  `prod_category` int(11) DEFAULT NULL,
  `prod_color` varchar(45) DEFAULT NULL,
  `prod_weight` double(5,2) DEFAULT '1.00',
  `prod_ind_postbus_size` tinyint(4) DEFAULT '0',
  `prod_available_qty` int(11) DEFAULT '0',
  `package_width` double(5,2) DEFAULT '1.00',
  `package_length` double(5,2) DEFAULT '1.00',
  `package_height` double(5,2) DEFAULT '1.00',
  PRIMARY KEY (`prod_id`),
  KEY `prod_cat_fk` (`prod_category`),
  CONSTRAINT `prod_cat_fk` FOREIGN KEY (`prod_category`) REFERENCES `categories` (`cat_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `product_images` (
  `image_id` int(11) NOT NULL,
  `prod_id` int(11) DEFAULT NULL,
  `prod_uri` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `img_prod_fk_idx` (`prod_id`),
  CONSTRAINT `img_prod_fk` FOREIGN KEY (`prod_id`) REFERENCES `products` (`prod_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
INSERT INTO `phoneplaats_webshop`.`hibernate_sequence`
(`next_val`)
VALUES
(1);

CREATE TABLE `indox_inventory` (
  `inventory_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `inventory_quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`inventory_id`),
  KEY `inv_prod_fk_idx` (`product_id`),
  CONSTRAINT `inv_prod_fk` FOREIGN KEY (`product_id`) REFERENCES `products` (`prod_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL,
  `salutation` varchar(45) DEFAULT NULL,
  `first_name` varchar(60) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `postcode` varchar(10) DEFAULT NULL,
  `street` varchar(100) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `house_no` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `func_id` varchar(45) DEFAULT NULL,
  `order_date` datetime DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `order_total` double DEFAULT NULL,
  `payment_id` varchar(100) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `shipping_cost` double DEFAULT NULL,
  `shippment_date` date DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `order_cust_fk_idx` (`customer_id`),
  CONSTRAINT `order_cust_fk` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE `order_details` (
  `order_detail_id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `prod_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `product_color` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`order_detail_id`),
  KEY `orderdetail_order_fk_idx` (`order_id`),
  KEY `FKj2yiyprjyi62xp0x965rbpd9` (`prod_id`),
  CONSTRAINT `FKj2yiyprjyi62xp0x965rbpd9` FOREIGN KEY (`prod_id`) REFERENCES `products` (`prod_id`),
  CONSTRAINT `detail_order_fk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

