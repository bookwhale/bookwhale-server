drop table if exists article_favorite;
create table if not exists article_favorite
(
    id                 bigint not null auto_increment comment '관심(좋아요) ID',
    user_id            bigint not null comment '사용자 id',
    article_id         bigint not null comment '판매글 id',
    created_date       timestamp comment '생성일시',
    last_modified_date timestamp comment '마지막 변경일시',
    primary key (id)
) comment '관심목록' engine = InnoDB;

alter table article_favorite
    add constraint article_fk_favorite
        foreign key (article_id)
            references article (id);

alter table article_favorite
    add constraint user_fk_favorite
        foreign key (user_id)
            references user (id);
