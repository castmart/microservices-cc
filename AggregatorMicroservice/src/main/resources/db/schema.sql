CREATE TABLE `product` (
    `id` varchar(36) NOT NULL,
    `name` varchar(30) NOT NULL,
    `description` varchar(90),
    `provider` varchar(30),
    `available` BOOLEAN NOT NULL DEFAULT FALSE,
    `measurementUnits` varchar(10),
    `creationTimestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `editionTimestamp` TIMESTAMP NULL on update CURRENT_TIMESTAMP,

    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
