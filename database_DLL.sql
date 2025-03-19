CREATE DATABASE IF NOT EXISTS ansible;
USE ansible;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

DROP TABLE IF EXISTS user;
CREATE TABLE user 
(
	id					int	 			not null auto_increment,
    username			varchar(50) 	not null,
    password			varchar(50) 	not null,
    phone				varchar(50) 	not null,
    email				varchar(50) 	not null,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory 
(
	id					int	 			not null auto_increment,
	filename 			varchar(100)	not null, 
    filepath 			varchar(500)	not null, 
    user_id				int,
    PRIMARY KEY(id),
    FOREIGN KEY(user_id) REFERENCES user(id) on delete cascade
);

DROP TABLE IF EXISTS playbook;
CREATE TABLE playbook 
(
	id					int	 			not null auto_increment,
	filename 			varchar(100)	not null, 
    filepath 			varchar(500), 
	user_id				int,
    PRIMARY KEY(id),
    FOREIGN KEY(user_id) REFERENCES user(id) on delete cascade
);


DROP TABLE IF EXISTS administrator;
CREATE TABLE administrator 
(
	id					int	 			not null auto_increment,
    username			varchar(50)    not null,
    password			varchar(50)	not null,
    PRIMARY KEY(id)
);

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1 */;

insert into administrator(username, password) values ('admin','admin');
insert into inventory(filename, filepath) values ('inventory', 'nesto');
insert into playbook(filename) values ('create_loopback0.yaml'), ('create_loopbacks.yaml'), ('delete_loopback0.yaml'), 
('interfaces_facts.yaml'), ('ios_facts.yaml'), ('list_interfaces.yaml');



