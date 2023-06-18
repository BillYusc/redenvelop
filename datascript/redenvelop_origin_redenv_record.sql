create table origin_redenv_record
(
    id          int auto_increment comment '红包id'
        primary key,
    user_id     int       not null comment '用户id，对应user_account主键',
    sum         int null comment '红包总额（单位分）',
    divisor     int       not null comment '红包份数',
    create_time timestamp not null comment '创建时间'
) comment '原始红包记录';