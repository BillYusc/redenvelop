package com.scc.redenvelop.controller;

import com.scc.redenvelop.dto.GrabRedenvRecordDto;
import com.scc.redenvelop.dto.OriginRedenvDto;
import com.scc.redenvelop.dto.R;
import com.scc.redenvelop.entity.GrabRedenvRecord;
import com.scc.redenvelop.limit.RateLimit;
import com.scc.redenvelop.service.AccountService;
import com.scc.redenvelop.service.RedenvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redenv")
public class RedenvController {
    @Autowired
    RedenvService redenvService;
    @Autowired
    AccountService accountService;

    /**
     * 发红包
     *
     * @param originRedenvDto 发出的红包信息
     * @return 发送结果
     */
    @PostMapping("/offer")
    @RateLimit(name = "发红包", prefix = "offerRedenv", count = 500, period = 1)
    public R<Object> offerRedenv(@RequestBody OriginRedenvDto originRedenvDto) {
        Integer userId = originRedenvDto.getUserId();
        Integer sum = originRedenvDto.getSum();
        Integer divisor = originRedenvDto.getDivisor();
        if (accountService.getRemaining(userId) < sum) {
            return R.builder().code(-1).message(userId + "余额不足").data(-1).build();
        } else if (sum < divisor || divisor < 1) {
            return R.builder().code(-1).message("非法红包").build();
        } else {
            Integer redPackId = redenvService.offerRedenv(originRedenvDto);
            return R.builder()
                    .code(1)
                    .message("用户" + userId + "红包发送成功，id为" + redPackId)
                    .data(redPackId)
                    .build();
        }
    }

    /**
     * 抢红包
     *
     * @param grabRedenvDto 抢红包
     * @return 抢红包结果
     */
    @PostMapping("/grab")
    @RateLimit(name = "抢红包", prefix = "grabRedenv", count = 500, period = 1)
    public R<Object> grabRedenv(@RequestBody GrabRedenvRecordDto grabRedenvDto) {
        GrabRedenvRecord result = new GrabRedenvRecord();
        try {
            if (redenvService.checkEnv(grabRedenvDto)) {
                result = redenvService.grab(grabRedenvDto);
            }
        } catch (Exception e) {
            return R.builder()
                    .code(1)
                    .message(e.getMessage())
                    .build();
        }
        return R.builder()
                .code(1)
                .message("手气不错，抢到了" + result.getAmount())
                .data(result.getAmount())
                .build();
    }

}
