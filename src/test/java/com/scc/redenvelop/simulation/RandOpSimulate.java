package com.scc.redenvelop.simulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scc.redenvelop.dto.GrabRedenvRecordDto;
import com.scc.redenvelop.dto.OriginRedenvDto;
import com.scc.redenvelop.dto.R;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.scc.redenvelop.utils.JsonObjectUtil.objToJson;

@Slf4j
public class RandOpSimulate {

    //放置红包id，用户id
    private static final List<Pair> packList = new ArrayList<>();
    //总用户数
    private static final int totalUser = 300;
    //金额上限
    private static final int redEnvelopLimit = 50000;
    //份数上限
    private static final int partsLimit = 20;
    //每个用户操作次数
    private static final int frequency = 100000;

    static Random r = new Random();

    /**
     * @param offerUserId 发红包的人id
     * @param sum         红包金额
     * @param divisor     份数
     */
    static void offer(Integer offerUserId, Integer sum, Integer divisor) {

        OriginRedenvDto o = new OriginRedenvDto();
        o.setUserId(offerUserId);
        o.setSum(r.nextInt(redEnvelopLimit));
        o.setDivisor(r.nextInt(partsLimit));
        String s = null;
        try {
            s = HttpRequestUtil.sendPostByJSON("http://localhost:8989/redenv/offer", objToJson(o));
        } catch (JsonProcessingException e) {
            log.error("用户{}:请求失败", offerUserId);
        }
        ObjectMapper mapper = new ObjectMapper();
        R<Integer> response = new R<>();
        try {
            response = mapper.readValue(s, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Integer id = response.getData();
        if (!id.equals(-1)) {
            packList.add(new Pair(id, offerUserId));
        }
        log.info("用户id{}：发包结果：{}", id, response.getMessage());
    }

    /**
     * @param grabUserId 抢红包的人id
     */
    static void grab(Integer grabUserId) {
        GrabRedenvRecordDto o = new GrabRedenvRecordDto();
        int grabUser = r.nextInt(totalUser) + 1;
        int i = r.nextInt(packList.size());
        o.setUserId(grabUserId);
        o.setOfferUserId(packList.get(i).getUserId());
        o.setRedEnvelopeId(packList.get(i).getPackid());
        String s = null;
        try {
            s = HttpRequestUtil.sendPostByJSON("http://localhost:8989/redenv/grab", objToJson(o));
        } catch (JsonProcessingException e) {
            log.error("用户{}:抢包请求失败", grabUserId);
        }

        ObjectMapper mapper = new ObjectMapper();
        R<Integer> response = new R<>();
        try {
            response = mapper.readValue(s, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer part = response.getData();
        log.info("用户{},抢包结果：{}", grabUser, response.getMessage());
    }

    /**
     * 模拟一个人的线程
     * 10%概率发红包
     * 90概率抢红包
     */
    static class Person extends Thread {
        Integer userId;

        public Person(Integer userId) {
            this.userId = userId;
        }

        @SneakyThrows
        @Override
        public void run() {
            for (int i = 0; i < frequency; i++) {
                int choice = r.nextInt(10);
                if (choice < 3) {
                    offer(userId, r.nextInt(redEnvelopLimit), r.nextInt(partsLimit));
                } else {
                    grab(userId);
                }
                sleep(1000);
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(totalUser);
        for (int i = 1; i <= totalUser; i++) {
            Thread thread = new Person(i);
            pool.submit(thread);
        }
    }
}
