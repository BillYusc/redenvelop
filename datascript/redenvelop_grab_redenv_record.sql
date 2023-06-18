create table grab_redenv_record
(
    id           int auto_increment comment '抢红包主键'
        primary key,
    env_id       int       not null comment '原始红包id，对应origin_redenv_record主键',
    amount       int null comment '抢到的部分金额',
    user_who_get int       not null comment '抢到红包的用户id',
    create_time  timestamp not null comment '抢到的时间'
) comment '抢红包记录';