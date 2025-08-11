create table user
(
    credit_quantity               integer,
    dob                           date                                                    not null,
    payment_method_expiring_date  date                                                    not null,
    credit_date                   datetime(6),
    id                            bigint                                                  not null auto_increment,
    plan_id                       bigint,
    email                         varchar(255)                                            not null unique,
    full_name                     varchar(255)                                            not null,
    identification_document_type  enum ('EIN','SSN')                                      not null,
    identification_document_value varchar(255)                                            not null unique,
    password                      varchar(255)                                            not null,
    payment_method_card_type      enum ('AMERICANEXPRESS','DISCOVER','MASTERCARD','VISA') not null,
    payment_method_number         varchar(255)                                            not null,
    payment_method_owner_name     varchar(255)                                            not null,
    payment_method_security_code  varchar(255)                                            not null,
    role                          enum ('ADMIN','USER')                                   not null,
    primary key (id),
    foreign key (plan_id) references plan (id)
) engine = InnoDB;