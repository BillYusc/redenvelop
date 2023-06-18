package com.scc.redenvelop.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GrabRedenvRecord {
    /**
     * 抢红包主键
     */
    private Integer id;

    /**
     * 原始红包id，对应origin_redenv_record主键
     */
    private Integer envId;

    /**
     * 抢到的部分金额
     */
    private Integer amount;

    /**
     * 抢到红包的用户id
     */
    private Integer userWhoGet;

    /**
     * 抢到的时间
     */
    private Date createTime;
}