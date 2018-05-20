CREATE TABLE `Settings` (
  `key` varchar(45) NOT NULL,
  `Value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`key`)
);
INSERT INTO `Settings`
(`key`,
`Value`)
VALUES
('Version',
'2.2.3');