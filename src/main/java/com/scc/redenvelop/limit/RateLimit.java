package com.scc.redenvelop.limit;


import java.lang.annotation.*;

/**
 * 限流注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RateLimit {

    // 资源名称，用于描述接口功能
    String name() default "";

    // 资源 key
    String key() default "";

    // key prefix
    String prefix() default "";

    // 时间的，单位秒
    int period() default 60;

    // 限制访问次数
    int count() default 60;

    // 限制类型
    LimitType limitType() default LimitType.KEY;

    enum LimitType {
        /**
         * 根据ip来作为限流的根据
         */
        IP,
        /**
         * 根据key来,不填默认以类名+方法名为key
         */
        KEY,

        /**
         * 同时根据ip和key来限流
         */
        IP_AND_KEY
    }
}
