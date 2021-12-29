drop table if exists token;
create table if not exists token
(
    email              varchar(255) not null,
    token_value        varchar(255) not null,
    created_date       timestamp,
    last_modified_date timestamp,
    primary key (email)
)engine = InnoDB;
