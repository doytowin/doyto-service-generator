CREATE DATABASE generator;

CREATE USER gen_user@localhost IDENTIFIED BY 'gen_pass';

GRANT ALL PRIVILEGES ON generator.* TO gen_user@localhost IDENTIFIED BY 'gen_pass';

flush privileges;