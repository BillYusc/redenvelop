package com.scc.redenvelop.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

@Slf4j
class RedEnvUtilTest {
    @Test
    void generate() {
        Random r = new Random();
        int sum = r.nextInt(20);
        int divisor = r.nextInt(10);
        log.debug("生成大小为{}分钱，发给{}个人的红包", sum, divisor);
        List<Integer> arr = RedEnvUtil.generate(sum, divisor);
        int expected = 0;
        for (int i = 0; i < arr.size(); i++) {
            expected += arr.get(i);
            log.debug("第{}个红包大小为{}分钱", i + 1, arr.get(i));
        }
        Assertions.assertEquals(expected, sum);
    }
}