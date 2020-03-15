DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;

CREATE TABLE members
(
    seq           bigint        NOT NULL AUTO_INCREMENT,
    email         varchar(50)   NOT NULL,
    password      varchar(200)  NOT NULL,
    name          varchar(30)   DEFAULT NULL,
    role          varchar(100)  DEFAULT NULL,
    last_login_at datetime      DEFAULT NULL,
    create_at     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (seq),
    CONSTRAINT unq_member_email UNIQUE (email)
);

CREATE TABLE books
(
    seq           bigint        NOT NULL AUTO_INCREMENT,
    id            varchar(220)  NOT NULL,
    category      varchar(10)   NOT NULL,
    name          varchar(200)  NOT NULL,
    description   varchar(360)  DEFAULT NULL,
    publish_date  date          NOT NULL,
    create_at     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (seq),
    CONSTRAINT unq_book_id UNIQUE (id)
);