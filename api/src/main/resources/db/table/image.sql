drop table if exists image;
create table if not exists image
(
    id         bigint not null auto_increment,
    article_id bigint not null,
    url        varchar(255),
    primary key (id)
) engine = InnoDB;

alter table image
    add constraint article_fk_image
        foreign key (article_id)
            references article (id);
