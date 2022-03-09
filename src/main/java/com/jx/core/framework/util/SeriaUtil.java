package com.jx.core.framework.util;

import java.io.*;
import java.util.Base64;

/**
 * @Type: SeriaUtil.java
 * @Author: zhangye
 * @Create_at: 2022/1/26 16:35
 * @Description:
 * @Version: 1.0
 */
public class SeriaUtil<T> {
    public SeriaUtil() {
    }

    public String seriaToBase64(T t) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(t);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public T derialFromBase64(String base64Code) {
        byte[] bytes = Base64.getDecoder().decode(base64Code);
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
