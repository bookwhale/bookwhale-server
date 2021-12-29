create table if not exists user
(
    id            bigint       not null auto_increment comment '사용자 키값',
    email              varchar(255) not null comment '사용자 이메일',
    nickname           varchar(255) not null comment '사용자 이름(닉네임)',
    profile_image      varchar(255) comment '사용자 프로필 URL',
    created_date       timestamp comment '생성일시',
    last_modified_date timestamp comment '수정일시',
    primary key (id),
    unique index idx_user_email (email)
) engine = InnoDB;
