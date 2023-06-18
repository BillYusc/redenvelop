package com.scc.redenvelop.service;

import com.scc.redenvelop.dto.IdRangeDto;
import com.scc.redenvelop.dto.PartRedEnvDto;

public interface AccountService {

    /**
     * 获取用户账户余额
     *
     * @param userId 用户id
     * @return 账户余额 单位：分
     */
    Integer getRemaining(Integer userId);

    /**
     * @param partRedEnvDto 抢到红包的信息
     */
    void income(PartRedEnvDto partRedEnvDto);

    Integer getLeftTotelMoney(IdRangeDto idRangeDto);
}
