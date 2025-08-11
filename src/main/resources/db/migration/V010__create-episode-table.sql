CREATE TABLE episode
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    number INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    synopsis VARCHAR(255) NOT NULL,
    duration INTEGER NOT NULL,
    season BIGINT NOT NULL,

    PRIMARY KEY(id),
    CONSTRAINT fk_episode_season_season_id
        FOREIGN KEY (season) references season(id),
    CONSTRAINT uk_episode_number
        UNIQUE (season, number)
) engine=InnoDB default charset=utf8;