package com.alt.config.mq;

/**
 * @author zx
 * @date 2022/9/4 23:25
 * @description MqConst
 */

public class MqConst {
    /**
     * 普通交换机
     */
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    /**
     * 死信交换机
     */
    public static final String DEAD_EXCHANGE = "dead_exchange";
    /**
     * 普通队列
     */
    public static final String NORMAL_QUEUE = "normal_queue";
    public static final String NORMAL_QUEUE2 = "normal_queue2";
    public static final String NORMAL_QUEUE3 = "normal_queue3";
    /**
     * 死信队列
     */
    public static final String DEAD_QUEUE = "dead_queue";

    public static final String MSG_CALLBACK = "msg_callback";

    public static final String RK = "n";
    public static final String RK2 = "n2";
    public static final String RK3 = "n3";
    public static final String RK_DEAD = "d";
    public static final String RK_CALLBACK = "msg_callback";
}
