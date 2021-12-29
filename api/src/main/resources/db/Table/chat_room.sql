drop table if exists chat_room;
create table if not exists chat_room
(
    id               bigint not null auto_increment,
    article_id       bigint not null,
    buyer_id         bigint not null,
    seller_id        bigint not null,
    is_buyer_delete  boolean,
    is_seller_delete boolean,
    created_date     timestamp,
    primary key (id)
) engine = InnoDB;

alter table chat_room
    add constraint article_fk_chatroom
        foreign key (article_id)
            references article (id);

alter table chat_room
    add constraint buyer_fk_chatroom
        foreign key (buyer_id)
            references user (id);

alter table chat_room
    add constraint seller_fk_chatroom
        foreign key (seller_id)
            references user (id);
