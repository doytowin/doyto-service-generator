ALTER TABLE generator.gen_project MODIFY name VARCHAR(50);
ALTER TABLE generator.gen_project ADD jdbcDriver VARCHAR(50) DEFAULT 'com.mysql.jdbc.Driver' NULL;
ALTER TABLE generator.gen_project ADD jdbcUrl VARCHAR(50) NULL;
ALTER TABLE generator.gen_project ADD jdbcUsername VARCHAR(100) NULL;
ALTER TABLE generator.gen_project ADD jdbcPassword VARCHAR(50) NULL;
