CREATE TABLE movie
(
    id       BIGINT  NOT NULL,
    duration INTEGER NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_movie_entertain_work_entertain_work_id FOREIGN KEY (id)
        REFERENCES entertain_work (id)
) ENGINE = InnoDB
  default charset = utf8;