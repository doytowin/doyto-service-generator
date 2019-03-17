ALTER TABLE gen_project ADD tablePrefix varchar(50) NULL;

ALTER TABLE gen_module ADD valid bit(1) DEFAULT TRUE NULL;
ALTER TABLE gen_module MODIFY tableName varchar(50);

ALTER TABLE gen_column ADD projectId int NULL;
ALTER TABLE gen_column
    MODIFY COLUMN projectId int AFTER id;
ALTER TABLE gen_column ADD valid bit(1) DEFAULT TRUE NULL;
ALTER TABLE gen_column ADD comment varchar(500) NULL;

CREATE UNIQUE INDEX uidx_gen_column_projectId_tableName_field ON gen_column (projectId, tableName, field);
