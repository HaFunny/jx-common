package com.jx.common.config;

import com.google.common.cache.CacheBuilder;
import com.jx.common.enums.ConcurrentCachesEnum;
import com.jx.core.cache.JxCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Type: JxConcurrentMapCacheConfig.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 14:19
 * @Description:
 * @Version: 1.0
 */
@Slf4j
@Configuration
public class JxConcurrentMapCacheConfig {
    /**
     * 配置示例缓存
     *
     * @return
     */
    @Bean("concurrentCacheManager")
    @Primary
    public CacheManager cacheManager() {
        JxCacheManager manager = new JxCacheManager();
        ArrayList<ConcurrentMapCache> caches = new ArrayList<>();
        for (ConcurrentCachesEnum c : ConcurrentCachesEnum.values()) {
            log.info("加载本地缓存名称:{}", c.name());
            caches.add(new ConcurrentMapCache(c.name()
                    , CacheBuilder.newBuilder()
                    .expireAfterWrite(c.getTtl(), TimeUnit.SECONDS)
                    .maximumSize(c.getMaxSize())
                    .build()
                    .asMap(), c.isAllowNullValues()));
        }
        manager.setCaches(caches);
        return manager;
    }
}
