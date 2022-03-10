package com.jx.core.mq;

/**
 * @Type: MQBusinessHandler.java
 * @Author: zhangye
 * @Create_at: 2022/3/10 16:21
 * @Description:
 * @Version: 1.0
 */
public interface MQBusinessHandler {
    boolean doBusiness(MQMessage<?> message);
}
