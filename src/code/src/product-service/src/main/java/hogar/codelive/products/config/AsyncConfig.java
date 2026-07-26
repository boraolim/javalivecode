package hogar.codelive.products.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${pool.size:10}")
    private Integer poolSize;

    @Value("${queue.capacity:100}")
    private Integer queueCapacity;

    /** Executor general de la aplicación — sin cambios */
    @Bean("asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("TaskThread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "dbExecutor")
    public Executor dbExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
