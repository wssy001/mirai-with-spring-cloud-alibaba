package cyou.wssy001.cloud.bot.service;

import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Service
public class RepetitiveGroupService {
    @Resource
    private ReactiveRedisOperations<String, RepetitiveGroup> reactiveRedisOperations;
    public static final String HASH_KEY = RepetitiveGroup.class.getSimpleName();

    public Mono<RepetitiveGroup> get(Long groupId) {
        return reactiveRedisOperations.opsForHash()
                .get(HASH_KEY, groupId)
                .map(v -> (RepetitiveGroup) v)
                .switchIfEmpty(Mono.defer(() -> Mono.just(new RepetitiveGroup())));
    }

    public Flux<RepetitiveGroup> getAll() {
        return reactiveRedisOperations.opsForHash()
                .values(HASH_KEY)
                .map(v -> (RepetitiveGroup) v);
    }

    public Mono<Boolean> upset(RepetitiveGroup repetitiveGroup) {
        return reactiveRedisOperations.opsForHash()
                .put(HASH_KEY, repetitiveGroup.getId(), repetitiveGroup);
    }

    public Mono<Boolean> delete(RepetitiveGroup repetitiveGroup) {
        return reactiveRedisOperations.opsForHash()
                .remove(HASH_KEY, repetitiveGroup.getId())
                .map(v -> v.equals(1L));
    }
}
