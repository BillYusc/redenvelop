package com.scc.redenvelop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
class RedenvelopApplicationTests {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    ExecutorService pool = Executors.newFixedThreadPool(1000);

    public static void main(String[] args) {

    }

    @Test
    void contextLoads() {
        Assertions.assertEquals(1, 2 - 1);
    }
}
