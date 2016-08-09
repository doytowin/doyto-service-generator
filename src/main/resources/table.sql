USE generator;

# DROP TABLE IF EXISTS GEN_User;
CREATE TABLE generator.GEN_User
(
    id           INT(11) PRIMARY KEY                 NOT NULL AUTO_INCREMENT,
    username     VARCHAR(50)                         NOT NULL,
    password     VARCHAR(300)                        NOT NULL,
    nickname     VARCHAR(50)                         NOT NULL,
    email        VARCHAR(100),
    mobile       VARCHAR(20),
    token        VARCHAR(63),
    score        INT(11),
    createTime   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    valid        BOOLEAN                                      DEFAULT TRUE,
    createUserId INT(11),
    rank         SMALLINT(6) DEFAULT '32767'         NOT NULL
);
CREATE UNIQUE INDEX email
    ON GEN_User (email);
CREATE UNIQUE INDEX mobile
    ON GEN_User (mobile);
CREATE UNIQUE INDEX nickname
    ON GEN_User (nickname);
CREATE UNIQUE INDEX username
    ON GEN_User (username);

# DROP TABLE IF EXISTS GEN_Project;
CREATE TABLE generator.GEN_Project
(
    id         INT(11)   NOT NULL AUTO_INCREMENT,
    userId     INT(11),
    name       VARCHAR(20),
    path       VARCHAR(300),
    createTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

# DROP TABLE IF EXISTS GEN_Module;
CREATE TABLE generator.GEN_Module
(
    id          INT(11)   NOT NULL AUTO_INCREMENT,
    projectId   INTEGER,
    name        VARCHAR(20),
    displayName VARCHAR(20),
    modelName   VARCHAR(20),
    fullName    VARCHAR(20),
    tableName   VARCHAR(20),
    createTime  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

# DROP TABLE IF EXISTS GEN_Template;
CREATE TABLE generator.GEN_Template
(
    id         INTEGER   NOT NULL AUTO_INCREMENT,
    projectId  INTEGER,
    suffix     VARCHAR(50),
    path       VARCHAR(300),
    cap        BOOLEAN            DEFAULT TRUE,
    content    LONGTEXT,
    valid      BOOLEAN   NOT NULL DEFAULT TRUE,
    createTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

# DROP TABLE IF EXISTS GEN_Path;
CREATE TABLE generator.GEN_Path
(
    moduleId   INT(11),
    templateId INT(11),
    path       VARCHAR(300)
);

# DROP TABLE IF EXISTS GEN_Column;
CREATE TABLE generator.GEN_Column
(
    id        INTEGER   NOT NULL AUTO_INCREMENT,
    tableName VARCHAR(50),
    field     VARCHAR(50),
    label     VARCHAR(50),
    type      VARCHAR(50),
    nullable  BOOLEAN,
    `key`     BOOLEAN,
    PRIMARY KEY (id)
);

