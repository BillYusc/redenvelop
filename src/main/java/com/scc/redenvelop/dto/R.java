package com.scc.redenvelop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * @Author ysc
 * @Description 请求返回体
 * @Comment //Comment
 * @Date 3:01 上午 2021/3/31
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class R<T> {
    private Integer code;
    private String message;
    private T data;
}
