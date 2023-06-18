package com.scc.redenvelop.controller;

import com.scc.redenvelop.dto.IdRangeDto;
import com.scc.redenvelop.dto.R;
import com.scc.redenvelop.dto.UserDto;
import com.scc.redenvelop.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    /**
     * 查询用户余额
     *
     * @param userDto 用户
     * @return 余额信息
     */
    @PostMapping("/remaining")
    public R<Object> checkRemaining(@RequestBody UserDto userDto) {
        int remaining = accountService.getRemaining(userDto.getUserId());
        return R.builder()
                .data(remaining)
                .code(1)
                .message(userDto.getUserName() + userDto.getUserId() + "余额是" + remaining).build();
    }

    /**
     * 查看id范围内未抢到的红包有多少
     *
     * @param idRangeDto 红包id范围
     * @return 剩余红包总额
     */
    @PostMapping("/leftmoney")
    public R<Object> getLeftTotelMoney(@RequestBody IdRangeDto idRangeDto) {
        Integer total = accountService.getLeftTotelMoney(idRangeDto);
        return R.builder().code(1).message("还剩" + total + "没被抢到").data(total).build();
    }
}
