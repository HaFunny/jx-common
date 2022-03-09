package com.jx.common.enums;

import com.jx.core.cache.JxCacheManager;

/**
 * @Type: ConcurrentCachesEnum.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 14:24
 * @Description: 定义cache名称，超时时长秒、最大个数
 * 每个cache缺省3600秒过期，最大个数1000
 * @Version: 1.0
 */
public enum ConcurrentCachesEnum {
    //字典值缓存
    DICT_CACHE(21600, 20, true);
    private long maxSize = JxCacheManager.DEFAULT_MAXSIZE;
    private long ttl;
    private boolean allowNullValues;

    ConcurrentCachesEnum(long maxSize, long ttl, boolean allowNullValues) {
        this.maxSize = maxSize;
        this.ttl = ttl;
        this.allowNullValues = allowNullValues;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public long getTtl() {
        return ttl;
    }

    public boolean isAllowNullValues() {
        return allowNullValues;
    }
}
