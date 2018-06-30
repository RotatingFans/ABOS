ALTER TABLE `orders`
CHANGE COLUMN `paid` `paid` DECIMAL(9,2) NULL DEFAULT '0' ;

UPDATE `orders` set `paid`=`Cost` WHERE `paid` = 1;
UPDATE `orders` set `paid`=0 WHERE `paid` = 0;
UPDATE `Settings` set `Value`='2.2.4' WHERE `key` = 'Version';