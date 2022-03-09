package com.jx.core.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Type: JxCacheManager.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 13:58
 * @Description:
 * @Version: 1.0
 */
public class JxCacheManager extends ConcurrentMapCacheManager {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    private static final long EXPIRE_TIME = 3600;
    public static final long DEFAULT_MAXSIZE = 1000;
    private long expireTime = EXPIRE_TIME;
    private long maximumSize = DEFAULT_MAXSIZE;

    public JxCacheManager() {
    }

    public JxCacheManager(long expireTime, long maximumSize) {
        if (expireTime > 0) {
            this.expireTime = expireTime;
        }
        if (maximumSize > 0) {
            this.maximumSize = maximumSize;
        }
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.computeIfAbsent(name, k -> createConcurrentMapCache(k));
        return cache;
    }

    @Override
    @Nullable
    protected Cache createConcurrentMapCache(String name) {
        //此处使用GOOGLE GUAVA的构造MANAGER方式
        return new ConcurrentMapCache(name, CacheBuilder.newBuilder()
                .expireAfterWrite(this.expireTime, TimeUnit.SECONDS)
                .maximumSize(this.maximumSize)
                .build()
                .asMap(), isAllowNullValues());
    }

    public void setCaches(Collection<? extends Cache> caches) {
        for (Cache cache : caches) {
            this.cacheMap.put(cache.getName(), cache);
        }
    }
}
