# user 테이블 생성
CREATE TABLE IF NOT EXISTS USER
(
    id                  bigint          NOT NULL AUTO_INCREMENT,
    username            varchar(10)     NOT NULL,
    email               varchar(100)    NOT NULL,
    created_datetime    datetime        NOT NULL,
    last_datetime       datetime        NOT NULL,
    PRIMARY KEY (id)
);
# DROP TABLE USER;

# plan 테이블 생성
CREATE TABLE IF NOT EXISTS PLAN
(
    id                  bigint          NOT NULL AUTO_INCREMENT,
    user_id             bigint          NOT NULL,
    title               varchar(100)    NOT NULL,
    content             varchar(500)    NOT NULL,
    password            varchar(10)     NOT NULL,
    created_datetime    datetime        NOT NULL,
    last_datetime       datetime        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES USER(id)
);
# DROP TABLE PLAN;
