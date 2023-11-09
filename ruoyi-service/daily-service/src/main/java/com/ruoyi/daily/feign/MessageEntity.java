package com.ruoyi.daily.feign;

import lombok.Data;

/**
 * @author zx
 * @date 2021/12/23 21:27
 */
@Data
public class MessageEntity {
    private String message;
    private String userId;
    private String procDefKey;
}
