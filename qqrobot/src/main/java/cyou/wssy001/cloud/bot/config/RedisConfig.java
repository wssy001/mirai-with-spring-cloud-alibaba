package cyou.wssy001.cloud.bot.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
    }

    @Bean
    public ReactiveRedisConnection connection(ReactiveRedisConnectionFactory connectionFactory) {
        return connectionFactory.getReactiveConnection();
    }

    @Bean
    ReactiveRedisOperations<String, Object> reactiveRedisOperations(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, getObjectRedisSerializationContext());
    }

    @Bean
    ReactiveSetOperations<String, Object> reactiveSetOperations(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, getObjectRedisSerializationContext()).opsForSet();
    }

    @Bean
    ReactiveZSetOperations<String, Object> reactiveZSetOperations(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, getObjectRedisSerializationContext()).opsForZSet();
    }

    private static RedisSerializationContext<String, Object> getObjectRedisSerializationContext() {
        return RedisSerializationContext
                .<String, Object>newSerializationContext(new GenericFastJsonRedisSerializer())
                .key(new FastJson2JsonRedisSerializer<>(String.class))
                .value(new FastJson2JsonRedisSerializer<>(Object.class))
                .hashKey(new FastJson2JsonRedisSerializer<>(String.class))
                .hashValue(new FastJson2JsonRedisSerializer<>(Object.class))
                .build();
    }
}
