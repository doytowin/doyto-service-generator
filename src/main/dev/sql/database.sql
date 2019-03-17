CREATE DATABASE generator default charset utf8 COLLATE utf8_general_ci;

CREATE USER gen_user@localhost IDENTIFIED BY 'gen_pass';

GRANT ALL PRIVILEGES ON generator.* TO gen_user@'localhost';

flush privileges;