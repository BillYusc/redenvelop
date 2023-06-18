package com.scc.redenvelop.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GrabRedenvRecordDto {
    /**
     * 抢红包用户id
     */
    private Integer userId;
    /**
     * 发红包用户id
     */
    private Integer offerUserId;
    /**
     * 红包id
     */
    private Integer redEnvelopeId;
    /**
     * 创建时间
     */
    private Date createTime;
}
