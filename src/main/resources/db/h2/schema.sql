DROP TABLE IF EXISTS members;

CREATE TABLE members (
  seq               bigint NOT NULL AUTO_INCREMENT,
  email             varchar(50) NOT NULL,
  password          varchar(100) NOT NULL,
  name              varchar(30) NOT NULL,
  last_login_at     datetime DEFAULT NULL,
  create_at         datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (seq),
  CONSTRAINT unq_member_email UNIQUE (email)
);