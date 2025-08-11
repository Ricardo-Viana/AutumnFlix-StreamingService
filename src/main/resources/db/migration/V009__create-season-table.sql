create table season (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    number   integer NOT NULL,
    series   bigint NOT NULL,
    synopsis varchar(255),

    PRIMARY KEY (id),
    CONSTRAINT fk_season_series_series_id
        FOREIGN KEY (series) REFERENCES series (id),
    CONSTRAINT uk_season_number
        UNIQUE (series, number)
) engine=InnoDB default charset=utf8;