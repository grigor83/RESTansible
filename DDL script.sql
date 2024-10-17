CREATE DATABASE IF NOT EXISTS ansible;
USE ansible;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
	id				int	 not null auto_increment,
    name			varchar(50)		not null,
    lastname		varchar(200)	not null,
	username		varchar(50)		not null,
    password		varchar(200)	not null,
    email			varchar(50)		not null,
    phone			varchar(50)		not null,
    PRIMARY KEY(id)
);