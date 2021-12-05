drop table if exists article;
create table if not exists article
(
    article_id            bigint not null auto_increment comment '판매글 키값',
    book_title         varchar(255) comment '책정보 - 도서명',
    book_author        varchar(255) comment '책정보 - 저자명',
    book_isbn          varchar(255) comment '책정보 - ISBN',
    book_list_price    varchar(255) comment '책정보 - 책가격',
    book_pub_date      varchar(255) comment '책정보 - 출판일',
    book_publisher     varchar(255) comment '책정보 - 출판사',
    book_summary       longtext comment '책정보 - 설명',
    book_thumbnail     varchar(255) comment '책정보 - 섬네일',
    title              varchar(255) comment '판매글 제목',
    article_status        varchar(255) comment '판매상태',
    book_status        varchar(255) comment '책상태',
    price              varchar(255) comment '판매가격',
    description        longtext comment '판매글 설명',
    seller_id          bigint comment '판매자 키값',
    like_count         bigint default 0 comment '판매글 관심(좋아요)수',
    view_count         bigint default 0 comment '판매글 조회수',
    created_date       timestamp comment '생성일시',
    last_modified_date timestamp comment '최종수정일',
    primary key (article_id)
) comment '판매글' engine = InnoDB;