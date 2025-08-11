create table entertain_work_genres
(
    entertain_work_id bigint not null,
    genre_id              bigint not null,

    primary key (entertain_work_id, genre_id),

    constraint fk_entertain_work_genres_genre_genre_id
        foreign key (genre_id) references genre (id),

    constraint fk_entertain_work_genres_entertain_work_entertain_work_id
        foreign key (entertain_work_id) references entertain_work (id)
) engine = InnoDB
  default charset = utf8;