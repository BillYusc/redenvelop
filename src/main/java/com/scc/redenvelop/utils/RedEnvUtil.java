package com.scc.redenvelop.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/***
 * @Author ysc
 * @Description 生成红包的工具类
 * @Date 12:45 上午 2021/3/31
 **/
@Slf4j
public class RedEnvUtil {

    /**
     * 红包key前缀
     */
    public static final String RED_ENV_ID_PERFIX = "scc:redenv:list:";

    /**
     * 红包锁key前缀
     */
    public static final String LOCK_PERFIX = "scc:redenv:lock";

    /**
     * 抢红包历史Key前缀
     */
    public static final String GRAB_HISTORY_PERFIX = "scc:redenv:history:";

    /**
     * 红包顺序zset Key
     */
    public static final String RED_ENV_LIST = "scc:redenv:zset";

    static Random r = new Random();

    private RedEnvUtil() {
    }

    /**
     * 红包生成算法，最小红包保证1分钱>_>
     *
     * @param sum     红包总额（单位：分）
     * @param divisor 红包份数
     * @return 分好的红包列表
     */
    public static List<Integer> generate(int sum, int divisor) {
        List<Integer> res = new ArrayList<>();
        //剩余值
        int remain = sum;
        Queue<Integer> queue = new PriorityQueue<>();
        //使用Set避免随机值重复发出0分钱红包
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < divisor - 1; i++) {
            int temp = r.nextInt(sum);
            while (temp == 0 || set.contains(temp)) {
                temp = r.nextInt(sum);
            }
            set.add(temp);
            queue.add(temp);
        }
        for (int i = 0; i < divisor - 1 && !queue.isEmpty(); i++) {
            int k = remain - (sum - queue.poll());
            remain -= k;
            res.add(k);
        }
        res.add(remain);
        log.info("总金额{}分钱的红包，分{}份,{}", sum, divisor, Arrays.toString(res.toArray()));
        //在队列末尾添加一个0,以区别是红包抢完，还是红包不存在
        res.add(0);
        return res;
    }

    /**
     * 获取红包key
     */
    public static String getKey(Integer redPackId) {
        return RED_ENV_ID_PERFIX + ':' + redPackId;
    }

    /**
     * 获取锁key
     */
    public static String getLockKey(Integer redPackId) {
        return LOCK_PERFIX + ':' + redPackId;
    }

    /**
     * 获取抢红包记录key
     */
    public static String getGrabHistory(Integer userId, Integer redPackId) {
        return GRAB_HISTORY_PERFIX + userId + ':' + redPackId;
    }
}
