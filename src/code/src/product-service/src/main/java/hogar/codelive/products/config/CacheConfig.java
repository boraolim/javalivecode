package hogar.codelive.products.config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.initialCapacity:100}")
    private Integer initialCapacity;

    @Value("${cache.maximumSize:1000}")
    private Integer maximumSize;

    @Value("${cache.expireAfterWrite:10}")
    private Integer expireAfterWrite;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("productSearchCache", "productByIdCache");
        cacheManager.setAsyncCacheMode(true);       
        
        cacheManager.setCaffeine(Objects.requireNonNull(Caffeine.newBuilder()
            .initialCapacity(initialCapacity)
            .maximumSize(maximumSize)
            .expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
            .recordStats()));
                
        return cacheManager;
    }
}
