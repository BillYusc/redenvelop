package com.scc.redenvelop.limit;

import com.scc.redenvelop.exception.LimitAccessException;

public interface IRateLimiter {
    void apply(String key, int limitCount, int limitPeriod, String descName) throws LimitAccessException;
}
