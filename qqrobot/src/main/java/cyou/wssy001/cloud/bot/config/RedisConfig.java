package cyou.wssy001.cloud.bot.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setHashKeySerializer(new FastJson2JsonRedisSerializer<>(String.class));
        redisTemplate.setHashValueSerializer(new FastJson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setKeySerializer(new FastJson2JsonRedisSerializer<>(String.class));
        redisTemplate.setValueSerializer(new FastJson2JsonRedisSerializer<>(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisConnection connection(RedisConnectionFactory connectionFactory) {
        return connectionFactory.getConnection();
    }

    @Bean
    public RedisOperations<String, Object> redisOperations(RedisConnectionFactory factory) {
        return redisTemplate(factory);
    }

    @Bean
    public HashOperations<String, Object, Object> hashOperations(RedisConnectionFactory factory) {
        return redisTemplate(factory).opsForHash();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisConnectionFactory factory) {
        return redisTemplate(factory).opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisConnectionFactory factory) {
        return redisTemplate(factory).opsForZSet();
    }

}
