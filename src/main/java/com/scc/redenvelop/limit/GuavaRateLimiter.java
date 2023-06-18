package com.scc.redenvelop.limit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.scc.redenvelop.exception.LimitAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * GuavaRateLimit
 */
@ConditionalOnProperty(prefix = "limiter", name = "type", havingValue = "guava")
@Component
@Slf4j
public class GuavaRateLimiter implements IRateLimiter {

    // 根据key分不同的令牌桶, 每3分钟自动清理缓存
    private static final Cache<String, RateLimiter> caches = CacheBuilder.newBuilder()
            //在访问后1分钟清除
            .expireAfterAccess(60, TimeUnit.SECONDS)
            //最大值,超过这个值会清除掉最近没使用到的缓存
            .maximumSize(1024)
            .build();

    @Override
    public void apply(String key, int limitCount, int limitPeriod, String descName) throws LimitAccessException {
        double limitPerSec = limitCount * 1.0 / limitPeriod;
        RateLimiter limiter = null;
        try {
            limiter = caches.get(key, () -> RateLimiter.create(limitPerSec));
            //1秒没获取到
        } catch (ExecutionException e) {
            log.error("获取限流出错: ", e);
        }
        if (limiter != null) {
            boolean b = limiter.tryAcquire(1, TimeUnit.SECONDS);
            if (!b) {
                log.info("限制访问key为 {}，描述为 [{}] 的接口", key, descName);
                throw new LimitAccessException("接口访问超出频率限制");
            }
        }
    }
}

