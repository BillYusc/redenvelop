package com.scc.redenvelop.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OriginRedenvRecord {
    /**
     * 红包id
     */
    private Integer packageId;

    /**
     * 用户id，对应user_account主键
     */
    private Integer userId;

    /**
     * 红包总额（单位分）
     */
    private Integer sum;

    /**
     * 红包份数
     */
    private Integer divisor;

    /**
     * 创建时间
     */
    private Date createTime;
}