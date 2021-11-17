create table if not exists user
(
    user_id            bigint       not null auto_increment,
    created_date       timestamp,
    last_modified_date timestamp,
    email              varchar(255) not null,
    identity           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    phone_number       varchar(255) not null,
    profile_image      varchar(255),
    role               varchar(255) not null,
    primary key (user_id)
) engine = InnoDB;

create table if not exists post
(
    post_id            bigint not null auto_increment,
    created_date       timestamp,
    last_modified_date timestamp,
    book_author        varchar(255),
    book_isbn          varchar(255),
    book_list_price    varchar(255),
    book_pub_date      varchar(255),
    book_publisher     varchar(255),
    book_summary       longtext,
    book_thumbnail     varchar(255),
    book_title         varchar(255),
    book_status        varchar(255),
    description        longtext,
    post_status        varchar(255),
    price              varchar(255),
    title              varchar(255),
    seller_id          bigint,
    primary key (post_id)
) engine = InnoDB;

create table if not exists image
(
    image_id bigint not null auto_increment,
    url      varchar(255),
    post_id  bigint not null,
    primary key (image_id)
) engine = InnoDB;

create table if not exists post_like
(
    like_id            bigint not null auto_increment comment '관심(좋아요) ID',
    created_date       timestamp comment '생성일시',
    last_modified_date timestamp comment '마지막 변경일시',
    post_id            bigint not null comment '판매글 id',
    user_id            bigint not null comment '사용자 id',
    primary key (like_id)
) comment '관심목록' engine = InnoDB;

create table if not exists chat_room
(
    room_id           bigint not null auto_increment,
    created_date      timestamp,
    buyer_leave_flag  boolean,
    seller_leave_flag boolean,
    buyer_id          bigint not null,
    post_id           bigint not null,
    seller_id         bigint not null,
    primary key (room_id)
) engine = InnoDB;

create table if not exists message
(
    message_id      bigint not null auto_increment,
    content         varchar(255),
    created_date    timestamp,
    room_id         bigint not null,
    sender_id       bigint not null,
    sender_identity varchar(255),
    primary key (message_id)
) engine = InnoDB;

alter table post
    add constraint post_fk_user
        foreign key (seller_id)
            references user (user_id)
            on delete cascade;

alter table image
    add constraint image_fk_post
        foreign key (post_id)
            references post (post_id)
            on delete cascade;

alter table post_like
    add constraint like_fk_post
        foreign key (post_id)
            references post (post_id)
            on delete cascade;

alter table post_like
    add constraint like_fk_user
        foreign key (user_id)
            references user (user_id)
            on delete cascade;

alter table chat_room
    add constraint chat_room_fk_buyer
        foreign key (buyer_id)
            references user (user_id);

alter table chat_room
    add constraint chat_room_fk_seller
        foreign key (seller_id)
            references user (user_id);

alter table chat_room
    add constraint chat_room_fk_post
        foreign key (post_id)
            references post (post_id);