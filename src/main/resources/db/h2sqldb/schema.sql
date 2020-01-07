DROP TABLE BOOK IF EXISTS;

CREATE TABLE BOOK (
    book_id         INTEGER IDENTITY PRIMARY KEY,
    title           VARCHAR(30),
    description     VARCHAR(300),
    category        VARCHAR(10),
    status          VARCHAR(16),
    publish_date    DATE,
    price           INTEGER
);

