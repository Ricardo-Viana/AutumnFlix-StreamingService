create table entertain_work
(
    release_year    integer                             not null,
    relevance       integer                             not null,
    id              bigint                              not null auto_increment,
    name            varchar(255)                        not null,
    parental_rating enum ('G','NC_17','PG','PG_13','R') not null,
    synopsis        varchar(255)                        not null,
    type            enum ('MOVIE','SERIES')              not null,

    primary key (id)
) engine = InnoDB
  default charset = utf8;


