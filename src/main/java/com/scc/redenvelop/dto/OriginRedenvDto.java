package com.scc.redenvelop.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OriginRedenvDto {

    private Integer userId;

    private Integer sum;

    private Integer divisor;

    private Date createTime;
}
