package cyou.localhost.cloud.bot.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setLockWatchdogTimeout(10000L);
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setPassword("localhost");
        singleServerConfig.setAddress("redis://localhost:6379");
        singleServerConfig.setDatabase(1);
        return Redisson.create(config);
    }
}
