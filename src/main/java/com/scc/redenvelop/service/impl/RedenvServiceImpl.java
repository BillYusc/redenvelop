package com.scc.redenvelop.service.impl;

import com.scc.redenvelop.dao.IGrabRedenvRecordDao;
import com.scc.redenvelop.dao.IOriginRedenvRecordDao;
import com.scc.redenvelop.dao.IUserAccountDao;
import com.scc.redenvelop.dto.GrabRedenvRecordDto;
import com.scc.redenvelop.dto.OriginRedenvDto;
import com.scc.redenvelop.dto.PartRedEnvDto;
import com.scc.redenvelop.entity.GrabRedenvRecord;
import com.scc.redenvelop.entity.OriginRedenvRecord;
import com.scc.redenvelop.exception.NoLeftException;
import com.scc.redenvelop.kafka.KafkaSender;
import com.scc.redenvelop.service.RedenvService;
import com.scc.redenvelop.utils.RedEnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.scc.redenvelop.utils.JsonObjectUtil.objToJson;
import static com.scc.redenvelop.utils.RedEnvUtil.*;

@Slf4j
@Service
public class RedenvServiceImpl implements RedenvService {
    @Autowired
    IUserAccountDao accountDao;
    @Autowired
    IOriginRedenvRecordDao originRedenvDao;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    KafkaSender kafkaSender;

    @Override
    @Transactional
    public Integer offerRedenv(OriginRedenvDto originRedenv) {
        accountDao.deduce(originRedenv);
        OriginRedenvRecord originRedenvRecord = new OriginRedenvRecord();

        Integer userId = originRedenv.getUserId();
        originRedenvRecord.setUserId(userId);
        originRedenvRecord.setDivisor(originRedenv.getDivisor());
        originRedenvRecord.setSum(originRedenv.getSum());
        originRedenvRecord.setCreateTime(new Date());
        originRedenvDao.offerRedenv(originRedenvRecord);

        Integer redPackId = originRedenvRecord.getPackageId();
        log.info("用户{}，发出红包id为{}", userId, redPackId);
        List<Integer> redPack = RedEnvUtil.generate(originRedenv.getSum(), originRedenv.getDivisor());
        //放入缓存
        redisTemplate.opsForList().rightPushAll(getKey(redPackId), redPack.toArray());
        //Zset实现延时队列
        redisTemplate.opsForZSet().add(RED_ENV_LIST, redPackId, System.currentTimeMillis());
        return redPackId;
    }

    @Override
    public GrabRedenvRecord grab(GrabRedenvRecordDto grabRedenv) throws Exception {
        Integer offerUserId = grabRedenv.getOfferUserId();
        Integer userId = grabRedenv.getUserId();
        Integer redEnvelopeId = grabRedenv.getRedEnvelopeId();
        String redPackKey = getKey(redEnvelopeId);
        String lockKey = getLockKey(redEnvelopeId);
        RLock lock = redissonClient.getLock(lockKey);
        RList<Object> list = redissonClient.getList(redPackKey);
        if (list.size() == 1) {
            //等于1说明被抢完
            throw new NoLeftException();
        }
        GrabRedenvRecord grabRedenvRecord = new GrabRedenvRecord();
        grabRedenvRecord.setCreateTime(new Date());
        grabRedenvRecord.setUserWhoGet(userId);
        grabRedenvRecord.setEnvId(redEnvelopeId);

        try {
            if (lock.tryLock(5, 5, TimeUnit.SECONDS)) {
                //抢到锁后需要再判断一次是否被抢完，因为加锁前判断时可能还有没抢完的红包，但抢到锁后已经被抢完
                if (redissonClient.getList(redPackKey).size() == 1) {
                    throw new NoLeftException();
                }
                Integer amount = (Integer) redisTemplate.opsForList().leftPop(redPackKey);
                //存放抢红包历史
                String grabHistory = getGrabHistory(offerUserId, redEnvelopeId);
                redisTemplate.opsForHash().put(grabHistory, userId.toString(), System.currentTimeMillis());
                grabRedenvRecord.setAmount(amount);
                log.info("用户{}抢到了用户{}发出的id为{}的红包，{}分钱",
                        userId,
                        offerUserId,
                        redEnvelopeId,
                        amount);
                PartRedEnvDto partRedEnvDto = new PartRedEnvDto(redEnvelopeId, userId, amount);
                kafkaSender.sendMsg("grab", objToJson(partRedEnvDto));
            }
        } finally {
            lock.unlock();
        }
        return grabRedenvRecord;
    }

    @Override
    public boolean checkEnv(GrabRedenvRecordDto grabRedenv) throws NoLeftException {
        Integer offerUserId = grabRedenv.getOfferUserId();
        Integer userId = grabRedenv.getUserId();
        Integer redEnvelopeId = grabRedenv.getRedEnvelopeId();
        String redPackKey = getKey(redEnvelopeId);
        RList<Object> list = redissonClient.getList(redPackKey);
        if (list.isEmpty()) {
            throw new NoLeftException(offerUserId + ":" + redEnvelopeId + "红包不存在！");
        }
        String grabHistory = getGrabHistory(offerUserId, redEnvelopeId);
        boolean grabed = redisTemplate.opsForHash().hasKey(grabHistory, userId.toString());
        if (grabed) {
            throw new NoLeftException("你已经抢过了！");
        }
        return true;
    }
}
