package com.scc.redenvelop.schedule;

import com.scc.redenvelop.dao.IUserAccountDao;
import com.scc.redenvelop.dto.PartRedEnvDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.scc.redenvelop.utils.RedEnvUtil.*;

@Component
@Slf4j
public class ExpireRedEnvTask {

    /**
     * 过期时间
     */
    static double duePeriod = 60000;
    private static Integer count = 0;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    IUserAccountDao accountDao;

    @Scheduled(fixedRate = 1000)
    public void expireRedEnv() {
        log.info("轮询zset，第{}次", count++);
        double d = (double) System.currentTimeMillis();
        Set<Object> set = redisTemplate.opsForZSet().rangeByScore(RED_ENV_LIST, 0, d - duePeriod);
        if (set != null) {
            for (Object o : set) {
                log.info("有{}个红包过期需要回收", set.size());
                Integer redPackId = (Integer) o;
                Integer userId = accountDao.getUserIdByRed(redPackId);
                Integer leftMoney = getRedEnvelopeLeft(redPackId);
                if(leftMoney >0){
                    PartRedEnvDto dto = new PartRedEnvDto();
                    dto.setUserId(userId);
                    dto.setRedEnvelopeId(redPackId);
                    dto.setAmount(leftMoney);
                    accountDao.income(dto);
                }
                redisTemplate.opsForZSet().remove(RED_ENV_LIST, redPackId);
                log.info("红包id {} 已过期，{}分钱已退回{}账户", redPackId, leftMoney, userId);
            }
        }
    }

    private Integer getRedEnvelopeLeft(Integer redPackId) {
        RLock lock = redissonClient.getLock(getLockKey(redPackId));
        String redEnvKey = getKey(redPackId);
        lock.lock();
        Integer res = 0;
        try {
            RList<Object> list = redissonClient.getList(redEnvKey);
            for (Object o : list) {
                res += (Integer) o;
            }
            redisTemplate.delete(redEnvKey);
        } finally {
            lock.unlock();
        }
        return res;
    }
}
