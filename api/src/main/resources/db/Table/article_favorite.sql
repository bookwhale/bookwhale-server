drop table if exists article_favorite;
create table if not exists article_favorite
(
    favorite_id            bigint not null auto_increment comment '관심(좋아요) ID',
    created_date       timestamp comment '생성일시',
    last_modified_date timestamp comment '마지막 변경일시',
    article_id            bigint not null comment '판매글 id',
    user_id            bigint not null comment '사용자 id',
    primary key (favorite_id)
) comment '관심목록' engine = InnoDB;