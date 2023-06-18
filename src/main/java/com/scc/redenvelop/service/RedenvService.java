package com.scc.redenvelop.service;

import com.scc.redenvelop.dto.GrabRedenvRecordDto;
import com.scc.redenvelop.dto.OriginRedenvDto;
import com.scc.redenvelop.entity.GrabRedenvRecord;
import com.scc.redenvelop.exception.NoLeftException;


public interface RedenvService {
    /**
     * 发红包
     *
     * @param originRedenvDto 红包信息
     * @return 生成的红包id
     */
    Integer offerRedenv(OriginRedenvDto originRedenvDto);

    /**
     * 抢红包
     *
     * @param grabRedenvDto 抢红包信息
     * @return 抢红包结果
     * @throws NoLeftException 红包已抢完
     */
    GrabRedenvRecord grab(GrabRedenvRecordDto grabRedenvDto) throws Exception;

    /**
     * 校验抢红包
     *
     * @param grabRedenvDto 抢红包信息
     */
    boolean checkEnv(GrabRedenvRecordDto grabRedenvDto) throws NoLeftException;
}
