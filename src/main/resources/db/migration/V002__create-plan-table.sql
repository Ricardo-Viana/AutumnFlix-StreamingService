create table plan
(
    id          bigint not null auto_increment,
    num_credits integer,
    value decimal(5,2) not null,
    description varchar(100),
    name        enum ('BASIC','MEDIUM','PREMIUM') not null,

    primary key (id)
) engine = InnoDB
  default charset = utf8;
