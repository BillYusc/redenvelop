package com.scc.redenvelop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartRedEnvDto {

    /**
     * 红包id
     */
    private Integer redEnvelopeId;
    /**
     * 抢到红包的用户id
     */
    private Integer userId;
    /**
     * 抢到金额 单位：分
     */
    private Integer amount;
}
