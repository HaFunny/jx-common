package com.jx.core.mq;

/**
 * @Type: MQProducer.java
 * @Author: zhangye
 * @Create_at: 2022/3/10 16:57
 * @Description:
 * @Version: 1.0
 */
public interface MQProducer {
    void start();

    void shutdown();

    boolean send(MQMessage<?> message);
}
