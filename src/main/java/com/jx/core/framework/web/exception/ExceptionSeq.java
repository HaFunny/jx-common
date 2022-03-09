package com.jx.core.framework.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * @Type: ExceptionSeq.java
 * @Author: zhangye
 * @Create_at: 2022/1/26 11:18
 * @Description:
 * @Version: 1.0
 */
@Component
public class ExceptionSeq {
    private Logger log = LoggerFactory.getLogger(ExceptionSeq.class);
    private String hostSign = "";
    private int seqNo = 0;

    public ExceptionSeq() {
    }

    public String getExceptionSeq() {
        return this.hostSign + String.valueOf(this.getNextSeq());
    }

    @PostConstruct
    private void init() {
        this.log.info("[jx]======ExceptionSeq.init======");
        this.hostSign = String.valueOf(this.getAID());
        this.log.info("hostSign=" + this.hostSign);
    }

    private int getAID() {
        boolean flag = false;

        int aid;
        try {
            InetAddress inet = InetAddress.getLocalHost();
            byte[] address = inet.getAddress();
            this.log.info("[jx]当前主机地址 => {}", inet.getHostAddress());
            aid = this.getInt(address);
        } catch (UnknownHostException e) {
            aid = new Random().nextInt(100000);
        }

        return aid;
    }

    private int getInt(byte[] bytes) {
        int size = bytes.length > 6 ? 6 : bytes.length;
        int result = 0;

        for (int i = size - 1; i >= 0; --i) {
            if (i == size - 1) {
                result += bytes[i];
            } else {
                result += bytes[i] << 4 * (size - 1 - i);
            }
        }

        return result > 0 ? result : -result;

    }

    private synchronized int getNextSeq() {
        int sn = this.seqNo++;
        if (sn > 100000) {
            this.seqNo = 0;
            sn = 0;
        }

        return sn;

    }
}
