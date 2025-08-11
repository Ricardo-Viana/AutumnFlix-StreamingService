create table series
(
    id bigint not null,

    primary key (id),
    constraint fk_series_entertain_work_entertain_work_id
        foreign key (id) references entertain_work (id)
) engine=InnoDB default charset = utf8;