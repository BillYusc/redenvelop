create table user_account
(
    id      int auto_increment comment '用户主键'
        primary key,
    account int default 10000 not null comment '用户余额'
) comment '账户表';