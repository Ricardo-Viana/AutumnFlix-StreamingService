create table user_watched_entertain_work (
    entertainment_work_id bigint not null,
    user_id bigint not null,

    constraint fk_user_watched_entertain_work_entertain_work_entertain_work_id
        foreign key (entertainment_work_id) references entertain_work (id),

    constraint fk_user_watched_entertain_work_user_user_id
        foreign key (user_id) references user (id)
) engine=InnoDB;