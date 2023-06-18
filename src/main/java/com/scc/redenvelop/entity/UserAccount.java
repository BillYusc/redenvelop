package com.scc.redenvelop.entity;

import lombok.Data;

@Data
public class UserAccount {
    /**
     * 用户主键
     */
    private Integer id;

    /**
     * 用户余额
     */
    private Integer account;
}