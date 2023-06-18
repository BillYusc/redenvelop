package com.scc.redenvelop.service.impl;

import com.scc.redenvelop.dao.IUserAccountDao;
import com.scc.redenvelop.dto.IdRangeDto;
import com.scc.redenvelop.dto.PartRedEnvDto;
import com.scc.redenvelop.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.scc.redenvelop.utils.RedEnvUtil.getKey;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    IUserAccountDao accountDao;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public Integer getRemaining(Integer userId) {
        return accountDao.getRemaining(userId);
    }

    @Override
    public void income(PartRedEnvDto partRedEnvDto) {
        accountDao.income(partRedEnvDto);
    }

    @Override
    public Integer getLeftTotelMoney(IdRangeDto idRangeDto) {
        Integer begin = idRangeDto.getBegin();
        Integer end = idRangeDto.getEnd();
        int res = 0;
        for (int i = begin; i <= end; i++) {
            RList<Integer> list = redissonClient.getList(getKey(i));
            int temp = 0;
            for (Integer a : list) {
                temp += a;
            }
            res += temp;
        }
        return res;
    }
}
