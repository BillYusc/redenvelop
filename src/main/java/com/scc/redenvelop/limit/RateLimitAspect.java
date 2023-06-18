package com.scc.redenvelop.limit;


import com.scc.redenvelop.exception.LimitAccessException;
import com.scc.redenvelop.utils.IpAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@ConditionalOnProperty(name = "limiter.type")
@Component
@Slf4j
public class RateLimitAspect {

    private final static String KEY_PREFIX = "limit";

    @Autowired
    IRateLimiter rateLimiter;

    //更换注解的class路径
    @Pointcut("@annotation(com.scc.redenvelop.limit.RateLimit)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        RateLimit limitAnnotation = method.getAnnotation(RateLimit.class);

        int limitPeriod = limitAnnotation.period();
        int limitCount = limitAnnotation.count();

        String name = limitAnnotation.name();
        //获取限流的key
        String key = getKey(request, signature);
        String redisKey = KEY_PREFIX + ':' +
                limitAnnotation.prefix() + ':' +
                key + ':' +
                DigestUtils.md5DigestAsHex(
                        (request.getRequestedSessionId() == null ? "" : request.getRequestedSessionId()).getBytes());

        try {
            rateLimiter.apply(redisKey, limitCount, limitPeriod, name);
        } catch (LimitAccessException e) {
            log.error(e.getMessage());
        }
        return point.proceed();
    }

    /**
     * 获取限流的key
     */
    private String getKey(HttpServletRequest request, MethodSignature signature) {
        Method method = signature.getMethod();
        RateLimit limitAnnotation = method.getAnnotation(RateLimit.class);
        RateLimit.LimitType limitType = limitAnnotation.limitType();
        String key;
        String customerKey = limitAnnotation.key();
        if (StringUtils.isEmpty(customerKey)) {
            //获取类名
            String className = signature.getClass().getSimpleName();
            //获取方法名
            String methodName = method.getName();
            customerKey = className + "@" + methodName;
        }
        switch (limitType) {
            case IP:
                key = IpAddressUtil.getIpAddress(request);
                break;
            case KEY:
                key = customerKey;
                break;
            case IP_AND_KEY:
                key = IpAddressUtil.getIpAddress(request) + "-" + customerKey;
                break;
            default:
                key = "";
        }
        return key.replace(":", ".");
    }
}

