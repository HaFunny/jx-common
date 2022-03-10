package com.jx.core.mq;

/**
 * @Type: MQConsumer.java
 * @Author: zhangye
 * @Create_at: 2022/3/10 16:58
 * @Description:
 * @Version: 1.0
 */
public interface MQConsumer {
    void start();

    void shutdown();

    void subscribe(String str, MQBusinessHandler mqBusinessHandler);

    void unsubscribe(String str);
}
