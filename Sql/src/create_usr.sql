CREATE DATABASE MAISON_DB;

CREATE USER 'maison_usr'@'localhost' IDENTIFIED BY 'allomaison';
GRANT ALL PRIVILEGES ON MAISON_DB.* TO 'maison_usr'@'localhost';
FLUSH PRIVILEGES;
