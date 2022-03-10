package com.jx.core.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @Type: MQMessage.java
 * @Author: zhangye
 * @Create_at: 2022/3/10 16:19
 * @Description:
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MQMessage<T> implements Serializable {
    private static final long serialVersionUID = 1290082498252320609L;
    private String id;
    private long timestamp;
    private String topic;
    private String tag;
    private Map<String, Object> headers;
    private T content;
}
